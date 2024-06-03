package com.actvn.cinema.service;

import com.actvn.cinema.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();

    Category getCategoryById(Integer categoryId);
}
