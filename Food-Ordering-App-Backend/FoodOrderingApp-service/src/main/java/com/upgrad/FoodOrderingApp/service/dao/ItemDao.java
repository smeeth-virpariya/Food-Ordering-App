package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class ItemDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * This method gets top five popular items of a restaurant.
   *
   * @param restaurant Restaurant whose top five items are to be queried.
   * @return top five items
   */
  public List<ItemEntity> getOrdersByRestaurant(RestaurantEntity restaurant) {
    List<ItemEntity> items =
        entityManager
            .createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class)
            .setParameter(0, restaurant.getId())
            .getResultList();
    if (items != null) {
      return items;
    }
    return Collections.emptyList();
  }

  public ItemEntity getItemByUUID(String itemUUID) {
    try {
      ItemEntity item =
          entityManager
              .createNamedQuery("itemByUUID", ItemEntity.class)
              .setParameter("itemUUID", itemUUID)
              .getSingleResult();
      return item;
    } catch (NoResultException nre) {
      return null;
    }
  }
}
