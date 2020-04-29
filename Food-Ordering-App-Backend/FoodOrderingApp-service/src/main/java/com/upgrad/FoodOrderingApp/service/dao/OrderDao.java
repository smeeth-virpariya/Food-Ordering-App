package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class OrderDao {
  @PersistenceContext private EntityManager entityManager;

  /**
   * Fetches the orders of the customer in a sorted manner with latest order being on the top. *
   *
   * @param customerUUID customer whose orders are to be fetched. * @return list of orders made by
   *     customer
   * @return list of orders made by customer.
   */
  public List<OrderEntity> getOrdersByCustomers(String customerUUID) {
    List<OrderEntity> ordersByCustomer =
        entityManager
            .createNamedQuery("getOrdersByCustomer", OrderEntity.class)
            .setParameter("customerUUID", customerUUID)
            .getResultList();
    if (ordersByCustomer != null) {
      return ordersByCustomer;
    }
    return Collections.emptyList();
  }
}
