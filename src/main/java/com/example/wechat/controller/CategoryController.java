package com.example.wechat.controller;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Category;
import com.example.wechat.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.Optional;
@RestController
@CrossOrigin
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "添加病类", notes = "添加新的病类，需要管理员权限")
    @PostMapping("/addCategory")
    public ResponseEntity<String> addCategory(
            @ApiParam(value = "病类信息", required = true) @RequestBody Category category,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Optional<Category> savedCategory = categoryService.addCategory(category);
                return ResponseEntity.ok(Result.okGetStringByData("病类添加成功", savedCategory));
            } catch (DefaultException de) {

                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }

    @ApiOperation(value = "删除病类", notes = "删除指定的病类，需要管理员权限。如果病类被引用，则更新引用为'待定'。")
    @DeleteMapping("/deleteCategoryById")
    public ResponseEntity<String> deleteCategoryById(
            @ApiParam(value = "病类ID", required = true) @RequestParam String categoryId,
            HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        if (userAuth == null || !"2".equals(userAuth)) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备管理员权限"));
        }

        try {
            ObjectId id = new ObjectId(categoryId);
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Result.okGetString("病类删除成功，相关引用已更新为'待定'"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("删除病类失败: " + e.getMessage()));
        }
    }




}
