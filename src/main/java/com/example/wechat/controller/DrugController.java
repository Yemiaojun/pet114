package com.example.wechat.controller;

import com.example.wechat.model.Drug;
import com.example.wechat.service.DrugService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.wechat.model.User;
import com.example.wechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/drugs")
public class DrugController {

    @Autowired
    private DrugService drugService;

    @ApiOperation(value="添加药品", notes = "添加新的药品记录，需要管理员权限")
    @PostMapping("/addDrug")
    public ResponseEntity<String> addDrug(@ApiParam(value = "药品信息", required = true) @RequestBody Drug drug, HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            Drug savedDrug = drugService.addDrug(drug);
            return ResponseEntity.ok(Result.okGetStringByData("药品添加成功", savedDrug));
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }

    @ApiOperation(value = "更新药品信息", notes = "根据提供的药品信息更新药品，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "药品更新成功"),
            @ApiResponse(code = 400, message = "药品更新失败，药品可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateDrug")
    public ResponseEntity<String> updateDrug(@ApiParam(value = "药品信息", required = true) @RequestBody Drug drug, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            Optional<Drug> updatedDrug = drugService.updateDrug(drug);
            if (updatedDrug.isPresent()) {
                return ResponseEntity.ok(Result.okGetStringByData("药品更新成功", updatedDrug.get()));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("药品更新失败，药品可能不存在"));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }
}