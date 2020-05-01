package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  @Autowired private ItemDao itemDao;


  /**
   * This method gets top five popular items of a restaurant.
   *
   * @param restaurantEntity Restaurant whose top five items are to be queried.
   * @return top five items
   */

  public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
    return itemDao.getOrdersByRestaurant(restaurantEntity);
  }

  public List<ItemEntity> getItemsInCategoryInRestaurant(Integer restaurantId, Integer categoryId) {

    List<ItemEntity> itemsInEachCategoryInRestaurant = itemDao.getAllItemsInCategoryInRestaurant(restaurantId,categoryId);
    return itemsInEachCategoryInRestaurant;
  }
}
