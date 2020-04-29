package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

  @Autowired private CouponDao couponDao;

  @Autowired private OrderDao orderDao;
  /**
   * This method contains business logic to get coupon details by coupon name.
   *
   * @param couponName
   * @return
   * @throws CouponNotFoundException if coupon with that name doesn't exist in database.
   */
  public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {

    if (couponName.isEmpty()) {
      throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
    }

    CouponEntity couponEntity = couponDao.getCouponByName(couponName.toUpperCase());

    if (couponEntity == null) {
      throw new CouponNotFoundException("CPF-001", "No coupon by this name");
    }

    return couponEntity;
  }

  /**
   * Fetches the orders of the customer in a sorted manner with latest order being on the top.
   *
   * @param customerUUID customer whose orders are to be fetched.
   * @return list of orders made by customer
   */
  public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
    return orderDao.getOrdersByCustomers(customerUUID);
  }
}
