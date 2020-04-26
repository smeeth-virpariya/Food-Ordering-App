package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

  @Autowired private OrderService orderService;

  @Autowired private CustomerService customerService;

  /**
   * This API endpoint gets coupon details by coupon name
   *
   * @param authorization
   * @param couponName
   * @return
   * @throws AuthorizationFailedException If authorization is not valid.
   * @throws CouponNotFoundException If coupon name doesn't exist in database
   */
  @CrossOrigin
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/order/coupon/{coupon_name}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CouponDetailsResponse> getCouponByName(
      @RequestHeader("authorization") final String authorization,
      @PathVariable("coupon_name") final String couponName)
      throws AuthorizationFailedException, CouponNotFoundException {

    String accessToken = Utility.getTokenFromAuthorization(authorization);

    customerService.getCustomer(accessToken);

    CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);

    CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
    couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
    couponDetailsResponse.setCouponName(couponEntity.getCouponName());
    couponDetailsResponse.setPercent(couponEntity.getPercent());

    return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
  }
}
