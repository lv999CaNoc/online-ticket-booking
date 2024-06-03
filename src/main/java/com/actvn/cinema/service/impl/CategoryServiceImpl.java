package com.actvn.cinema.service.impl;

import com.actvn.cinema.model.Category;
import com.actvn.cinema.repositories.CategoryRepository;
import com.actvn.cinema.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Integer categoryId){
        return categoryRepository.findById(categoryId).get();
    }
}
