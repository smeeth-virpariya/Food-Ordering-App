package com.upgrad.FoodOrderingApp.service.businness;


import java.util.List;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {


    @Autowired
    public CategoryDao categoryDao;


    public CategoryEntity getCategoryEntity(final String uuid) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(uuid);
        if(categoryEntity == null){
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }
        return categoryEntity;
    }

    public List<CategoryEntity> getAllCategories() {
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategories();
        return categoryEntities;
    }
}