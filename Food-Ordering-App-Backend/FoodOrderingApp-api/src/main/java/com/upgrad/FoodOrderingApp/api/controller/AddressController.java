package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.*;
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

import java.util.List;
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
      @RequestBody(required = false) final SaveAddressRequest saveAddressRequest,
      @RequestHeader("authorization") final String authorization)
      throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException {

    final String accessToken = Utility.getTokenFromAuthorization(authorization);
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    final AddressEntity addressEntity = new AddressEntity();
    addressEntity.setUuid(UUID.randomUUID().toString());
    addressEntity.setCity(saveAddressRequest.getCity());
    addressEntity.setLocality(saveAddressRequest.getLocality());
    addressEntity.setPincode(saveAddressRequest.getPincode());
    addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
    addressEntity.setActive(1);
    addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));

    final AddressEntity savedAddress = addressService.saveAddress(addressEntity, customerEntity);
    SaveAddressResponse saveAddressResponse =
        new SaveAddressResponse()
            .id(savedAddress.getUuid())
            .status("ADDRESS SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
  }

  /**
   * This api endpoint is used retrieves all the saved addresses of a customer from the database.
   *
   * @param authorization customer login access token in 'Bearer <access-token>' format.
   * @return ResponseEntity<AddressListResponse> type object along with HttpStatus as OK.
   */
  @CrossOrigin
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/address/customer",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AddressListResponse> getAllAddress(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    final String accessToken = Utility.getTokenFromAuthorization(authorization);
    final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    final List<AddressEntity> addressEntityList = addressService.getAllAddress(customerEntity);

    final AddressListResponse addressListResponse = new AddressListResponse();

    for (AddressEntity addressEntity : addressEntityList) {
      AddressList addressResponseList =
          new AddressList()
              .id(UUID.fromString(addressEntity.getUuid()))
              .flatBuildingName(addressEntity.getFlatBuilNo())
              .city(addressEntity.getCity())
              .pincode(addressEntity.getPincode())
              .locality(addressEntity.getLocality())
              .state(
                  new AddressListState()
                      .id(UUID.fromString(addressEntity.getState().getUuid()))
                      .stateName(addressEntity.getState().getStateName()));
      addressListResponse.addAddressesItem(addressResponseList);
    }
    return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
  }
}
