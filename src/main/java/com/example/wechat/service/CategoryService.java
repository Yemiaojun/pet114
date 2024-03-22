package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Category;
import com.example.wechat.repository.CategoryRepository;
import com.example.wechat.repository.DiseaseRepository;
import com.example.wechat.repository.QuestionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 添加类别的业务逻辑
    public Optional<Category> addCategory (Category category) {
       // 检查是否已经存在相同名称的类别
         Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
            if (existingCategory.isPresent()) {
                // 类别名称已存在，返回空Optional作为错误指示
                throw new DefaultException("本类名已存在");
            }

            // 类别名称不存在，添加新类别
            Category savedCategory = categoryRepository.save(category);
            return Optional.of(savedCategory);
    }


    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private QuestionRepository questionRepository;


    public Category ensurePendingCategoryExists() {
        Category pendingCategory = new Category();
        pendingCategory.setName("待定");

        // 尝试添加"待定"类别，如果它已经存在，这个方法将不会抛出异常，而是返回已存在的类别
        try {
            return addCategory(pendingCategory).orElseThrow(() -> new DefaultException("无法创建或找到'待定'类别"));
        } catch (DefaultException e) {
            // 如果因为类别名已存在而抛出异常，则尝试返回现有的"待定"类别
            return categoryRepository.findByName("待定")
                    .orElseThrow(() -> new DefaultException("无法找到'待定'类别，且尝试创建时出错"));
        }
    }

    public void deleteCategory(ObjectId categoryId) {
        // 确保存在一个"待定"的Category
        Category pendingCategory = ensurePendingCategoryExists();

        // 更新引用了即将删除Category的Disease文档
        diseaseRepository.findByCategoryId(categoryId).forEach(disease -> {
            disease.setCategory(pendingCategory);
            diseaseRepository.save(disease);
        });

        // 更新引用了即将删除Category的Question文档
        questionRepository.findByCategoryId(categoryId).forEach(question -> {
            question.setCategory(pendingCategory);
            questionRepository.save(question);
        });

        // 删除指定的Category文档
        categoryRepository.deleteById(categoryId);
    }
}