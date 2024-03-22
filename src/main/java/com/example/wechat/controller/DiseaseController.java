package com.example.wechat.controller;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Drug;
import com.example.wechat.service.DiseaseService;
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

/**
 * DiseaseController 提供了与疾病相关的操作的控制器。
 */
@RestController
@RequestMapping("/disease")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;


    /**
     * 添加新的疾病信息。
     *
     * @param disease 疾病信息
     * @param session HTTP 会话
     * @return 添加成功后的疾病信息
     */
    @ApiOperation(value="添加疾病", notes = "添加新的疾病，需要管理员权限")
    @PostMapping("/addDisease")
    public ResponseEntity<String> addDisease(
            @ApiParam(value = "疾病信息", required = true) @RequestBody Disease disease,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
            Disease savedDisease = diseaseService.addDisease(disease);
            return ResponseEntity.ok(Result.okGetStringByData("疾病添加成功", savedDisease));
            }catch (DefaultException de){

                    return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }


    /**
     * 删除疾病信息。
     *
     * @param name    要删除的疾病名称
     * @param session HTTP 会话
     * @return 删除成功后的疾病信息
     */
    @ApiOperation(value="删除疾病", notes = "删除疾病，需要管理员权限")
    @PostMapping("/deleteDisease")
    public ResponseEntity<String> deleteDisease(
            @ApiParam(value = "疾病名字", required = true) @RequestBody String name,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
            Optional<Disease> dd = diseaseService.deleteDiseaseByName(name);
            return ResponseEntity.ok(Result.okGetStringByData("疾病删除成功",dd));
            }catch (DefaultException de){
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }


    /**
     * 更新疾病信息。
     *
     * @param disease 要更新的疾病信息
     * @param session HTTP 会话
     * @return 更新后的疾病信息
     */
    @ApiOperation(value = "更新疾病信息", notes = "根据提供的疾病信息更新疾病，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "疾病更新成功"),
            @ApiResponse(code = 400, message = "疾病更新失败，疾病可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateDisease")
    public ResponseEntity<String> updateDisease(@ApiParam(value = "疾病信息", required = true) @RequestBody Disease disease, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try{
            Optional<Disease> updatedDisease = diseaseService.updateDisease(disease);
            if (updatedDisease.isPresent()) {
                return ResponseEntity.ok(Result.okGetStringByData("疾病更新成功", updatedDisease.get()));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("疾病更新失败，药品可能不存在"));
            }}catch (DefaultException de){
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }
}
