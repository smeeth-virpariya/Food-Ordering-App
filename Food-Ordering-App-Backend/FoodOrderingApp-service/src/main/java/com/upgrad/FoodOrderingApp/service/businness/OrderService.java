package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  @Autowired private CouponDao couponDao;

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
}
