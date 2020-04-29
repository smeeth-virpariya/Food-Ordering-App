package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.api.model.OrderList;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddress;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddressState;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.OrderListCustomer;
import com.upgrad.FoodOrderingApp.api.model.OrderListPayment;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

  @CrossOrigin
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/order",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CustomerOrderResponse> getOrdersByCustomer(
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException {

    String accessToken = Utility.getTokenFromAuthorization(authorization);

    // Identify customer from the access token.
    CustomerEntity customerEntity = customerService.getCustomer(accessToken);

    // Get all the orders of the customer.
    List<OrderEntity> ordersOfCustomer =
        orderService.getOrdersByCustomers(customerEntity.getUuid());

    List<OrderList> orders = new ArrayList<>();
    for (OrderEntity orderEntity : ordersOfCustomer) {
      OrderList order = new OrderList();
      order.setId(UUID.fromString(orderEntity.getUuid()));
      order.setDate(orderEntity.getDate().toString());
      order.setBill(new BigDecimal(orderEntity.getBill()));
      order.setDiscount(new BigDecimal(orderEntity.getDiscount()));
      order.setCustomer(getOrderListCustomer(orderEntity.getCustomer()));
      order.setCoupon(getOrderListCoupon(orderEntity.getCoupon()));
      order.setAddress(getOrderListAddress(orderEntity.getAddress()));
      order.setPayment(getOrderListPayment(orderEntity.getPayment()));
      List<OrderItemEntity> orderItems = orderEntity.getOrderItems();
      order.setItemQuantities(getItemQuantityResponseList(orderItems));
      orders.add(order);
    }

    CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
    customerOrderResponse.setOrders(orders);
    return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
  }

  private OrderListCustomer getOrderListCustomer(CustomerEntity customer) {
    OrderListCustomer orderListCustomer = new OrderListCustomer();
    orderListCustomer.setId(UUID.fromString(customer.getUuid()));
    orderListCustomer.setFirstName(customer.getFirstName());
    orderListCustomer.setLastName(customer.getLastName());
    orderListCustomer.setEmailAddress(customer.getEmailAddress());
    orderListCustomer.setContactNumber(customer.getContactNumber());
    return orderListCustomer;
  }

  private OrderListCoupon getOrderListCoupon(CouponEntity coupon) {
    OrderListCoupon orderListCoupon = new OrderListCoupon();
    orderListCoupon.setId(UUID.fromString(coupon.getUuid()));
    orderListCoupon.setCouponName(coupon.getCouponName());
    orderListCoupon.setPercent(coupon.getPercent());
    return orderListCoupon;
  }

  private OrderListPayment getOrderListPayment(PaymentEntity payment) {
    OrderListPayment orderListPayment = new OrderListPayment();
    orderListPayment.setId(UUID.fromString(payment.getUuid()));
    orderListPayment.setPaymentName(payment.getPaymentName());
    return orderListPayment;
  }

  private OrderListAddress getOrderListAddress(AddressEntity address) {
    OrderListAddress orderListAddress = new OrderListAddress();
    orderListAddress.setId(UUID.fromString(address.getUuid()));
    orderListAddress.setFlatBuildingName(address.getFlatBuildingNumber());
    orderListAddress.setLocality(address.getLocality());
    orderListAddress.setCity(address.getCity());
    orderListAddress.setPincode(address.getPincode());
    OrderListAddressState orderListAddressState = new OrderListAddressState();
    orderListAddressState.setId(UUID.fromString(address.getState().getUuid()));
    orderListAddressState.setStateName(address.getState().getStateName());
    orderListAddress.setState(orderListAddressState);
    return orderListAddress;
  }

  private List<ItemQuantityResponse> getItemQuantityResponseList(List<OrderItemEntity> items) {
    List<ItemQuantityResponse> responseList = new ArrayList<>();

    for (OrderItemEntity orderItem : items) {
      ItemQuantityResponse response = new ItemQuantityResponse();

      ItemQuantityResponseItem responseItem = new ItemQuantityResponseItem();
      responseItem.setId(UUID.fromString(orderItem.getItem().getUuid()));
      responseItem.setItemName(orderItem.getItem().getItemName());
      responseItem.setItemPrice(orderItem.getItem().getPrice());
      ItemQuantityResponseItem.TypeEnum itemType =
          Integer.valueOf(orderItem.getItem().getType()) == 0
              ? ItemQuantityResponseItem.TypeEnum.VEG
              : ItemQuantityResponseItem.TypeEnum.NON_VEG;
      responseItem.setType(itemType);
      response.setItem(responseItem);

      response.setQuantity(orderItem.getQuantity());
      response.setPrice(orderItem.getPrice());
      responseList.add(response);
    }
    return responseList;
  }
}
