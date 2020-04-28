package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

  /**
   * fetches all the addresses of a given customer.
   *
   * @param customer whose detals to be fetched.
   * @return List of CustomerAddressEntity type object.
   */
  public List<CustomerAddressEntity> customerAddressByCustomer(CustomerEntity customer) {
    try {
      return entityManager
          .createNamedQuery("customerAddressByCustomer", CustomerAddressEntity.class)
          .setParameter("customer", customer)
          .getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
