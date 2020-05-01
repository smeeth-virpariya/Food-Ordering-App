package com.upgrad.FoodOrderingApp.api.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
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
