package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

  @Autowired private ItemDao itemDao;

  public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
    return itemDao.getOrdersByRestaurant(restaurantEntity);
  }
}
