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
        // 判断病种的名字是否为空
        if (category.getName() == null) {
            throw new DefaultException("类别名不能为空");
        }

       // 检查是否已经存在相同名称的类别
         Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
            if (existingCategory.isPresent()) {
                // 类别名称已存在，返回空Optional作为错误指示
                throw new DefaultException("本类名已存在");
            }

            //判断名字的长度是否小于等于20
            if (category.getName().length() > 20) {
                throw new DefaultException("类别名长度不能超过20");
            }

            //判断名字是否都是汉字，字母，数字
            if (!category.getName().matches("[\\u4e00-\\u9fa5_a-zA-Z0-9]+")) {
                throw new DefaultException("类别名只能包含汉字、字母和数字");
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

    //删除类别的业务逻辑
    public Optional<Category> deleteCategory(ObjectId id) {
          Optional<Category> existingCategory = categoryRepository.findById(id);

          //对应名字的category不存在则报错
          if (!existingCategory.isPresent()) {
                // id不存在，返回空Optional作为错误指示
                throw new DefaultException("对应类别不存在");
          }
          //查找该病类下的疾病和问题，如果有则更新为'待定'
            Category pendingCategory = ensurePendingCategoryExists();
            diseaseRepository.findByCategoryId(id).forEach
                    (disease -> {
                        disease.setCategory(pendingCategory);
                        diseaseRepository.save(disease);
                    });
            questionRepository.findByCategoryId(id).forEach
                    (question -> {
                        question.setCategory(pendingCategory);
                        questionRepository.save(question);
                    });

          categoryRepository.delete(existingCategory.get());
          return existingCategory;
     }

    // 更新类别的业务逻辑
    public Optional<Category> updateCategory(Category updatedCategory) {
        // 检查类别是否存在
        Optional<Category> existingCategory = categoryRepository.findById(updatedCategory.getId());
        if (!existingCategory.isPresent()) {
            throw new DefaultException("类别不存在");
        }

        // 判断类别名是否为空
        if (updatedCategory.getName() == null) {
            throw new DefaultException("类别名不能为空");
        }

        //判断名字的长度是否小于等于20
        if (updatedCategory.getName().length() > 20) {
            throw new DefaultException("类别名长度不能超过20");
        }

        //判断名字是否都是汉字，字母，数字
        if (!updatedCategory.getName().matches("[\\u4e00-\\u9fa5_a-zA-Z0-9]+")) {
            throw new DefaultException("类别名只能包含汉字、字母和数字");
        }

        // 检查是否已经存在相同名称的类别
        Optional<Category> existingCategoryName = categoryRepository.findByName(updatedCategory.getName());
        if (existingCategoryName.isPresent() && !existingCategoryName.get().getId().equals(updatedCategory.getId())) {
            // 类别名称已存在，返回空Optional作为错误指示
            throw new DefaultException("本类名已存在");
        }

        // 类别存在，执行更新操作
        Category savedCategory = categoryRepository.save(updatedCategory);
        return Optional.of(savedCategory);
    }

    // 查找所有类别的业务逻辑
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 根据类别名称查找类别的业务逻辑
    public Optional<Category> getCategoryById(ObjectId id) {
        return categoryRepository.findById(id);
    }

    // 根据类别名称模糊查找类别的业务逻辑
    public Iterable<Category> getCategoriesByNameLike(String name) {
        return categoryRepository.findByNameLike(name);
    }



}