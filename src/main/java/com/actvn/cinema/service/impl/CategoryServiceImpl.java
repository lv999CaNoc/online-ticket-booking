package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Category;
import com.actvn.cinema.repositories.CategoryRepository;
import com.actvn.cinema.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() throws NotFoundException {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            log.warn("[LOG] Không có thể loại phim");
            throw new NotFoundException("Không có thể loại phim");
        }
        return categories;
    }

    @Override
    public Category getCategoryById(Integer categoryId) throws NotFoundException {
        Category category = categoryRepository.findById(categoryId).get();
        if (category == null){
            log.warn("[LOG] Không có thể loại phim có ID: {}", categoryId);
            throw new NotFoundException("Không có thể loại phim có ID: "+ categoryId);
        }
        return category;
    }
}
