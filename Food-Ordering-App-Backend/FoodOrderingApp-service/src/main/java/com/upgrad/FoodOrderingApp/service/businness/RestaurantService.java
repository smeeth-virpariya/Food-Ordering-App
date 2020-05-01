package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * This method gets the restaurant details.
     *
     * @param uuid UUID of the restaurant.
     * @return
     * @throws RestaurantNotFoundException if restaurant with UUID doesn't exist in the database.
     */
    public RestaurantEntity restaurantByUUID(String uuid) throws RestaurantNotFoundException {
        if(uuid == null){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurant = restaurantDao.restaurantByUUID(uuid);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurant;
    }

    /**
     * Gets all the restaurants in  DB.
     *
     * @return List of RestaurantEntity
     */
    public List<RestaurantEntity> getAllRestaurants() {


        return restaurantDao.getRestaurants();


    }

    /**
     * Gets  restaurants in  DB based on search string.
     *
     * @return List of RestaurantEntity
     */
    public List<RestaurantEntity> getAllRestaurantsBySearchString(final String search) throws RestaurantNotFoundException {
        if (search == null) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        List<RestaurantEntity> relevantRestaurantEntities = restaurantDao.getRestaurantsBySearchString(search);


        return relevantRestaurantEntities;
    }

    /**
     * Gets all the restaurants in  DB based on Category Uuid
     *
     * @return List of RestaurantEntity
     */
    public List<RestaurantEntity> getAllRestaurantsByCategory(final String categoryUuid) throws CategoryNotFoundException {
        if (categoryUuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity searchCategoryEntity = categoryDao.getCategoryByUuid(categoryUuid);
        if( searchCategoryEntity == null){
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }
        List<RestaurantEntity> restaurantEntities = searchCategoryEntity.getRestaurants();

        return restaurantEntities;


    }
}
