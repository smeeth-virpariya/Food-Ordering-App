package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class RestaurantDao {
  @PersistenceContext private EntityManager entityManager;

  public RestaurantEntity restaurantByUUID(String uuid) {
    try {
      return entityManager
          .createNamedQuery("restaurantByUUID", RestaurantEntity.class)
          .setParameter("uuid", uuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
