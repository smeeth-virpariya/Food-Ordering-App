package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

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

  /**
   * This method gets lists of all restaurants
   *
   * @param
   * @return List of RestaurantEntity
   */
  public List<RestaurantEntity> getRestaurants() {
    return entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class).getResultList();
  }


  /**
   * This method gets lists of all restaurants by Search string
   *
   * @param searchString
   * @return List of RestaurantEntity
   */
  public List<RestaurantEntity> getRestaurantsBySearchString(final String searchString) {
    return entityManager.createNamedQuery("getRestaurantBySearchString", RestaurantEntity.class)
            .setParameter("searchString" ,"%"+searchString+"%")
            .getResultList();
  }

  /**
   * This method updates the rating for a restaurant
   *
   * @param restaurantEntity
   * @return restaurantEntity
   */

  public RestaurantEntity updateRestaurantEntity(final RestaurantEntity restaurantEntity) {
    return entityManager.merge(restaurantEntity);
  }
}
