package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * AddressDao class provides the database access for all the endpoints inside the address controller
 */
@Repository
public class AddressDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Creates the address entity using the given AddressEntity.
   *
   * @param addressEntity contains the address details.
   * @return AddressEntity object.
   */
  public AddressEntity createCustomerAddress(final AddressEntity addressEntity) {
    entityManager.persist(addressEntity);
    return addressEntity;
  }
}
