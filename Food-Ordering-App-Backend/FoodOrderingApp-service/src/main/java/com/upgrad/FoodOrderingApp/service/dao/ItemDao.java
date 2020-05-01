package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

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

    public List<ItemEntity> getAllItemsInCategoryInRestaurant(Integer restaurantId, Integer categoryId) {
      List<ItemEntity> items =
              entityManager
                      .createNamedQuery("getAllItemsInCategoryInRestaurant", ItemEntity.class)
                      .setParameter(0,restaurantId )
                      .setParameter(1,categoryId)
                      .getResultList();
      if (items != null) {
        return items;
      }
      return Collections.emptyList();
    }
}
