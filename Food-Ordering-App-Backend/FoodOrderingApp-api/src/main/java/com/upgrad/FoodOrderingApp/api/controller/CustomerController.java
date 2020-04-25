package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * This api endpoint is used to signup a new customer
     *
     * @param signupCustomerRequest this argument contains all the attributes required for creating a new customer in the database.
     * @return ResponseEntity<SignupCustomerResponse> type object along with HttpStatus CREATED.
     * @throws SignUpRestrictedException if customer entered details not meet the requirements.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        // Validation for required fields if any field other than lastname is empty then it throws SignUpRestrictedException exception
        if (signupCustomerRequest.getFirstName().equals("") || signupCustomerRequest.getEmailAddress().equals("") || signupCustomerRequest.getContactNumber().equals("") || signupCustomerRequest.getPassword().equals("")) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmailAddress(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse customerResponse = new SignupCustomerResponse()
                .id(createdCustomerEntity.getUuid())
                .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }

    /**
     * This api endpoint is used to login the customer.
     *
     * @param authorization customer contactNumber and password in 'Basic Base64<contactNumber:password>' format.
     * @return ResponseEntity<LoginResponse> type object with HttpStatus as OK.
     * @throws AuthenticationFailedException if customer contactNumber or password is wrong.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode;
        String contactNumber;
        String password;
        try {
            decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            contactNumber = decodedArray[0];
            password = decodedArray[1];
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException exception) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerAuthEntity createdCustomerAuthEntity = customerService.authenticate(contactNumber, password);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(createdCustomerAuthEntity.getCustomer().getUuid());
        loginResponse.setFirstName(createdCustomerAuthEntity.getCustomer().getFirstName());
        loginResponse.setLastName(createdCustomerAuthEntity.getCustomer().getLastName());
        loginResponse.setContactNumber(createdCustomerAuthEntity.getCustomer().getContactNumber());
        loginResponse.setEmailAddress(createdCustomerAuthEntity.getCustomer().getEmailAddress());
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", createdCustomerAuthEntity.getAccessToken());
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

}
