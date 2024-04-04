package com.example.wechat.controller;

import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Drug;
import com.example.wechat.model.Facility;
import com.example.wechat.service.DrugService;
import com.example.wechat.service.FileStorageService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
@CrossOrigin
@RequestMapping("/drugs")
public class DrugController {

    @Autowired
    private DrugService drugService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 添加新的药品信息。
     *
     * @param drug 设备信息
     * @param session HTTP 会话
     * @return 添加成功后的药品信息
     */
    @ApiOperation(value="添加药品", notes = "添加新的药品，需要管理员权限")
    @PostMapping("/addDrug")
    public ResponseEntity<String> addDrug(
            @ApiParam(value = "药品信息", required = true) @RequestBody Drug drug,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Drug savedDrug = drugService.addDrug(drug);
                return ResponseEntity.ok(Result.okGetStringByData("药品添加成功", savedDrug));
            }catch (NameAlreadyExistedException naee){

                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));

            }
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


    /**
     * 删除药品信息。
     *
     * @param payload    请求体，其中包含设备id
     * @param session HTTP 会话
     * @return 删除成功后的药品信息
     */
    @ApiOperation(value="删除药品", notes = "删除药品，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "药品id", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteDrug")
    public ResponseEntity<String> deleteDrug(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 从请求体中获取部门id
        String id = payload.get("id");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Optional<Drug> optionalDrug = drugService.deleteDrugById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("药品删除成功",optionalDrug));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }


    /**
     * 根据药品名称模糊查找药品信息。
     *
     * @param name    药品名称
     * @param session HTTP 会话
     * @return 符合条件的设施列表的 ResponseEntity
     */
    @ApiOperation(value = "根据药品名字模糊查找药品", notes = "返回符合条件的药品列表，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取药品信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findDrugByName")
    public ResponseEntity<String> findDrugByName(
            @ApiParam(name = "name", value = "药品名称", required = true, example = "john") @RequestParam("name") String name,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查找权限"));
        }

        List<Drug> drugs = drugService.findDrugsByNameLike(name);

        return ResponseEntity.ok(Result.okGetStringByData("获取药品信息成功", drugs));
    }


    /**
     * 获取所有药品信息。
     *
     * @param session HTTP 会话
     * @return 所有药品列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有药品", notes = "返回所有设备列表,需要登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有药品信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findAllDrugs")
    public ResponseEntity<String> findAllDrugs(HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (!"1".equals(userId)) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }

        List<Drug> drugs = drugService.findAllDrugs();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有药品信息成功", drugs));
    }



    /**
     * 根据药品ID获取药品信息。
     *
     * @param id 药品ID
     * @param session HTTP会话
     * @return ResponseEntity 包含药品信息的响应实体
     */
    @ApiOperation(value = "根据药品id获取药品", notes = "返回对应实体，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有设备信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findDrugById")
    public ResponseEntity<String> findDrugById(
            @ApiParam(name = "id", value = "药品id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查找权限"));
        }

        try{
        Optional<Drug> drug = drugService.findDrugById(new ObjectId(id));
        return ResponseEntity.ok(Result.okGetStringByData("获取药品信息成功", drug));}catch (IdNotFoundException infe){
            return ResponseEntity.badRequest().body(Result.errorGetString(infe.getMessage()));

        }
    }


    /**
     * 上传药品图像。
     *
     * @param file 药品图像文件
     * @param id 药品id
     * @param session HTTP 会话
     * @return 包含图片更新结果的 ResponseEntity
     */
    @ApiOperation(value = "上传药品图像", notes = "上传药品图片，需要管理员权限")
    @PostMapping("/uploadPic")
    public ResponseEntity<String> uploadPic(
            @ApiParam(value = "设施药品文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "药品id", required = true) @RequestParam String id,
            HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr == null) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查找权限"));
        }
        try {
            ObjectId drugId = new ObjectId(id);
            Drug drug = drugService.findDrugById(drugId).get();
            String filePath = fileStorageService.storeDrugPic(file, file.getName());

            drugService.updateDrugPicUrl(drugId, filePath);
            return ResponseEntity.ok(Result.okGetStringByData("图片更新成功", filePath));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("图片更新失败: " + e.getMessage()));
        }
    }



}