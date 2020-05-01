package com.upgrad.FoodOrderingApp.api.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantUpdatedResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestaurantController {


    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;
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
    (@PathVariable("category_id")final String categoryUuid) throws CategoryNotFoundException {

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
     * This API endpoint gets  restaurant  for given restaurant UUID
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
        List<CategoryList> categories = getAllCategoryItemsInRestaurant(restaurantEntity);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();

        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new
                RestaurantDetailsResponseAddress();
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new
                RestaurantDetailsResponseAddressState();
        AddressEntity addressEntity = restaurantEntity.getAddress();
        restaurantDetailsResponseAddress.setId(UUID.fromString(addressEntity.getUuid()));
        restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
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

    /**
     * This API endpoint updates the restaurant rating by customer
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = ("/restaurant/{restaurant_id}/update"),
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantRating
    (@RequestHeader("authorization")final String authorization, @PathVariable("restaurant_id")final String restaurantUuid, final Double customerRating)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {
        String accessToken = Utility.getTokenFromAuthorization(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity,customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        restaurantUpdatedResponse.setId(UUID.fromString(updatedRestaurantEntity.getUuid()));
        restaurantUpdatedResponse.setStatus("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return  new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);

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
            restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
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


    private void sortAlphabetically(List<RestaurantList> allRestaurantsList){
        Collections.sort(allRestaurantsList, new Comparator<RestaurantList>() {
            @Override
            public int compare(RestaurantList r1, RestaurantList r2) {
                return r1.getRestaurantName().compareTo(r2.getRestaurantName());
            }

        });
    }


    private List<CategoryList> getAllCategoryItemsInRestaurant(final RestaurantEntity restaurantEntity){
        List<CategoryList> allCategoryItems = new ArrayList<>();
        List<CategoryEntity> categories = restaurantEntity.getCategories();
        sortCategoriesAlphabetically(categories);
        for (CategoryEntity c: categories){
            CategoryList categoryList = new CategoryList();
            categoryList.setId(UUID.fromString(c.getUuid()));
            categoryList.setCategoryName(c.getCategoryName());
            List<ItemList> allItemsInCategory =
                    getAllItemsInCategoryInRestaurant(restaurantEntity.getId(),c.getId());
            categoryList.setItemList(allItemsInCategory);
            allCategoryItems.add(categoryList);
        }

        return  allCategoryItems;
    }

    private void sortCategoriesAlphabetically(List<CategoryEntity> categoryEntities){
        Collections.sort(categoryEntities, new Comparator<CategoryEntity>() {
            @Override
            public int compare(CategoryEntity c1, CategoryEntity c2) {
                return c1.getCategoryName().compareTo(c2.getCategoryName());
            }

        });
    }

    private List<ItemList> getAllItemsInCategoryInRestaurant(final Integer restaurantId, final Integer categoryId){
        List<ItemList> itemsInCategoryInRestaurant = new ArrayList<>();
        List<ItemEntity> items = itemService.getItemsInCategoryInRestaurant(restaurantId,categoryId);
        for(ItemEntity i :items){
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(i.getUuid()));
            itemList.setItemName(i.getItemName());
            itemList.setPrice(i.getPrice());
            if (i.getType().equals("0")) {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("VEG"));
            } else {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("NON_VEG"));
            }

            itemsInCategoryInRestaurant.add(itemList);
        }

        return itemsInCategoryInRestaurant;
    }


}
