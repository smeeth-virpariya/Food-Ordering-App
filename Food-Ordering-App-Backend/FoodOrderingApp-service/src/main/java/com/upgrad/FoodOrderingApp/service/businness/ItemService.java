package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

  public ItemEntity getItemByUUID(String itemUUID) throws ItemNotFoundException {
    ItemEntity item = itemDao.getItemByUUID(itemUUID);
    if (item == null) {
      throw new ItemNotFoundException("INF-003","No item by this id exist");
    }
    return item;
  }
}
