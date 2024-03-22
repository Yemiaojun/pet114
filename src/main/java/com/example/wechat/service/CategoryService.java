package com.example.wechat.service;

import com.example.wechat.model.Category;
import com.example.wechat.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    // 添加类别的业务逻辑
    public Optional<Category> addCategory (Category category) {
       // 检查是否已经存在相同名称的类别
         Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
            if (existingCategory.isPresent()) {
                // 类别名称已存在，返回空Optional作为错误指示
                return Optional.empty();
            }

            // 类别名称不存在，添加新类别
            Category savedCategory = categoryRepository.save(category);
            return Optional.of(savedCategory);
    }




}