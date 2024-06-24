package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAll() throws NotFoundException;

    Category getCategoryById(Integer categoryId) throws NotFoundException;
}
