package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AddressController {

  @Autowired private CustomerService customerService;

  @Autowired private AddressService addressService;

  /**
   * This api endpoint is used to save address of a customer in the database.
   *
   * @param authorization customer login access token in 'Bearer <access-token>' format.
   * @return ResponseEntity<SaveAddressResponse> type object along with HttpStatus as Ok.
   * @throws AuthorizationFailedException if any of the validation on customer access token fails.
   */
  @CrossOrigin
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/address",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SaveAddressResponse> saveAddress(
      @RequestHeader("authorization") final String authorization,
      @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
      throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException {

    final String accessToken = Utility.getTokenFromAuthorization(authorization);
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    final AddressEntity addressEntity = new AddressEntity();
    addressEntity.setUuid(UUID.randomUUID().toString());
    addressEntity.setCity(saveAddressRequest.getCity());
    addressEntity.setLocality(saveAddressRequest.getLocality());
    addressEntity.setPincode(saveAddressRequest.getPincode());
    addressEntity.setFlatBuildingNumber(saveAddressRequest.getFlatBuildingName());
    addressEntity.setActive(1);
    addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));

    final AddressEntity savedAddress = addressService.saveAddress(addressEntity, customerEntity);
    SaveAddressResponse saveAddressResponse =
        new SaveAddressResponse()
            .id(savedAddress.getUuid())
            .status("ADDRESS SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
  }
}
