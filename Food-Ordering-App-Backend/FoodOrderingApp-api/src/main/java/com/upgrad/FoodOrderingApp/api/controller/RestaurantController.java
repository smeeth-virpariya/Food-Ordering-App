package com.upgrad.FoodOrderingApp.api.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;


    /**
     * This API endpoint gets list of all restaurant in order of their ratings
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurants() {

        List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurants();
        List<RestaurantList> allRestaurantsList = getRestaurantList(allRestaurants);
        sortBasedOnRating(allRestaurantsList);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(allRestaurantsList);

        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }


    /**
     * This API endpoint gets list of all restaurant found for given search string
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = ("/restaurant/name/{restaurant_name}"),
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsBySearchString
                        (@PathVariable("restaurant_name")final String restaurantName) throws RestaurantNotFoundException {

        List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurantsBySearchString(restaurantName);
        List<RestaurantList> allRestaurantsList = getRestaurantList(allRestaurants);
        sortAlphabetically(allRestaurantsList);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(allRestaurantsList);

        if(allRestaurantsList.isEmpty()) {
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);

    }


    /**
     * This API endpoint gets list of all restaurant found for given category UUID
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = ("/restaurant/category/{category_id}"),
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategory
           (@PathVariable("category_id")final String categoryUuid) throws  CategoryNotFoundException {

        List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurantsByCategory(categoryUuid);
        List<RestaurantList> allRestaurantsList = getRestaurantList(allRestaurants);
        sortAlphabetically(allRestaurantsList);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(allRestaurantsList);

        if(allRestaurantsList.isEmpty()) {
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);

    }


    /**
     * This API endpoint gets list of all restaurant found for given category UUID
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = ("/restaurant/{restaurant_id}"),
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantById
    (@PathVariable("restaurant_id")final String restaurantUuid) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        System.out.println("Restaurant found is : " + restaurantEntity.getRestaurantName());

        List<CategoryList> categories = getAllCategoryItemsInRestaurant(restaurantEntity);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();

        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new
                RestaurantDetailsResponseAddress();
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new
                RestaurantDetailsResponseAddressState();
        AddressEntity addressEntity = restaurantEntity.getAddress();
        restaurantDetailsResponseAddress.setId(UUID.fromString(addressEntity.getUuid()));
        restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
        restaurantDetailsResponseAddress.setCity(addressEntity.getCity());
        restaurantDetailsResponseAddress.setLocality(addressEntity.getLocality());
        restaurantDetailsResponseAddress.setPincode(addressEntity.getPincode());

        restaurantDetailsResponseAddressState.setId(UUID.fromString(addressEntity.getState().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(addressEntity.getState().getStateName());
        restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);
        restaurantDetailsResponse.setAddress(restaurantDetailsResponseAddress);

        restaurantDetailsResponse.setAveragePrice(restaurantEntity.getAvgPrice());
        restaurantDetailsResponse.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
        restaurantDetailsResponse.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated());
        restaurantDetailsResponse.setPhotoURL(restaurantEntity.getPhotoUrl());
        restaurantDetailsResponse.setRestaurantName(restaurantEntity.getRestaurantName());
        restaurantDetailsResponse.setCategories(categories);


        return new ResponseEntity<>(restaurantDetailsResponse, HttpStatus.OK);

    }

   private List<CategoryList> getAllCategoryItemsInRestaurant(final RestaurantEntity restaurantEntity){
            List<CategoryList> allCategoryItems = new ArrayList<>();
            List<CategoryEntity> categories = restaurantEntity.getCategories();
            for (CategoryEntity c: categories){
                CategoryList categoryList = new CategoryList();
                categoryList.setId(UUID.fromString(c.getUuid()));
                categoryList.setCategoryName(c.getCategoryName());
                List<ItemList> allItemsInCategory = getAllItemsInCategoryInRestaurant(restaurantEntity.getId(),c.getId());
                allCategoryItems.add(categoryList);
            }

      return  allCategoryItems;
   }

    private List<ItemList> getAllItemsInCategoryInRestaurant(final Integer restaurantId, final Integer categoryId){
            List<ItemList> itemsInCategoryInRestaurant = new ArrayList<>();
            List<ItemEntity> items = itemService.getItemsInCategoryInRestaurant(restaurantId,categoryId);
            for(ItemEntity i :items){
                ItemList itemList = new ItemList();


                itemsInCategoryInRestaurant.add(itemList);
            }

            return itemsInCategoryInRestaurant;
    }

    private List<RestaurantList> getRestaurantList(final List<RestaurantEntity> allRestaurants){
        List<RestaurantList> allRestaurantsList = new ArrayList<>();
        for (RestaurantEntity r : allRestaurants) {
            RestaurantList restaurantList = new RestaurantList();
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new
                    RestaurantDetailsResponseAddress();
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new
                    RestaurantDetailsResponseAddressState();
            restaurantList.setId(UUID.fromString(r.getUuid()));

            AddressEntity addressEntity = r.getAddress();
            restaurantDetailsResponseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlatBuildingNumber());
            restaurantDetailsResponseAddress.setCity(addressEntity.getCity());
            restaurantDetailsResponseAddress.setLocality(addressEntity.getLocality());
            restaurantDetailsResponseAddress.setPincode(addressEntity.getPincode());

            restaurantDetailsResponseAddressState.setId(UUID.fromString(addressEntity.getState().getUuid()));
            restaurantDetailsResponseAddressState.setStateName(addressEntity.getState().getStateName());

            restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);

            restaurantList.setAddress(restaurantDetailsResponseAddress);
            restaurantList.setAveragePrice(r.getAvgPrice());

            Set categories = new TreeSet();
            for (CategoryEntity c : r.getCategories()) {
                categories.add(c.getCategoryName());
            }
            StringBuilder categoriesString = new StringBuilder();
            for (Object c : categories) {
                categoriesString.append(  c + ", ");
            }
            restaurantList.setCategories(categoriesString.toString().replaceAll(", $", ""));

            restaurantList.setCustomerRating(BigDecimal.valueOf(r.getCustomerRating()));
            restaurantList.setNumberCustomersRated(r.getNumberCustomersRated());
            restaurantList.setPhotoURL(r.getPhotoUrl());
            restaurantList.setRestaurantName(r.getRestaurantName());
            allRestaurantsList.add(restaurantList);

        }


        return allRestaurantsList;

    }

    private void sortBasedOnRating(List<RestaurantList> allRestaurantsList){
        Collections.sort(allRestaurantsList, new Comparator<RestaurantList>() {
                    @Override
                    public int compare(RestaurantList r1, RestaurantList r2) {
                        return r2.getCustomerRating().compareTo(r1.getCustomerRating());
                    }

                });
    }

    private void sortAlphabetically(List<RestaurantList> allRestaurantsList){
        Collections.sort(allRestaurantsList, new Comparator<RestaurantList>() {
            @Override
            public int compare(RestaurantList r1, RestaurantList r2) {
                return r1.getRestaurantName().compareTo(r2.getRestaurantName());
            }

        });
    }
}
