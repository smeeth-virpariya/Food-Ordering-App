package com.upgrad.FoodOrderingApp.api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * This API endpoint gets list of all restaurant in order of their ratings
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getCategories() {

        List<CategoryEntity> allcategories = categoryService.getAllCategories();

        List<CategoryListResponse> categoryListResponses = new ArrayList<>();

        for(CategoryEntity c:allcategories){
            CategoryListResponse categoryListResponse = new CategoryListResponse();
            categoryListResponse.setCategoryName(c.getCategoryName());
            categoryListResponse.setId(UUID.fromString(c.getUuid()));
            categoryListResponses.add(categoryListResponse);
        }
        sortCategoriesAlphabetically(categoryListResponses);
        return new ResponseEntity<>(categoryListResponses, HttpStatus.OK);
    }

    private void sortCategoriesAlphabetically(List<CategoryListResponse> categoryListResponses) {

        Collections.sort(categoryListResponses, new Comparator<CategoryListResponse>() {
            @Override
            public int compare( CategoryListResponse c1, CategoryListResponse c2) {
                return c1.getCategoryName().compareTo(c2.getCategoryName());
            }

        });
    }
}
