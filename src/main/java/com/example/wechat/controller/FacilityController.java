package com.example.wechat.controller;


import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Facility;
import com.example.wechat.model.User;
import com.example.wechat.service.DepartmentService;
import com.example.wechat.service.FacilityService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;


    /**
     * 添加新的设备信息。
     *
     * @param facility 设备信息
     * @param session HTTP 会话
     * @return 添加成功后的设备信息
     */
    @ApiOperation(value="添加设备", notes = "添加新的设备，需要管理员权限")
    @PostMapping("/addFacility")
    public ResponseEntity<String> addFacility(
            @ApiParam(value = "设备信息", required = true) @RequestBody Facility facility,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Facility savedFacility = facilityService.addFacility(facility);
                return ResponseEntity.ok(Result.okGetStringByData("设备添加成功", savedFacility));
            }catch (NameAlreadyExistedException naee){

                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }




    /**
     * 删除设备信息。
     *
     * @param payload    请求体，其中包含设备id
     * @param session HTTP 会话
     * @return 删除成功后的设备信息
     */
    @ApiOperation(value="删除设备", notes = "删除设备，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "部门", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteFacility")
    public ResponseEntity<String> facilityDepartment(
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
                Optional<Facility> optionalFacility = facilityService.deleteFacilityById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("科室删除成功",optionalFacility));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }




    /**
     * 更新设备信息。
     *
     * @param facility 要更新的设备信息
     * @param session HTTP 会话
     * @return 更新后的设备信息
     */
    @ApiOperation(value = "更新设备信息", notes = "根据提供的设备信息更新设备，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "设备更新成功"),
            @ApiResponse(code = 400, message = "设备更新失败，设备可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateFacility")
    public ResponseEntity<String> updateFacility(@ApiParam(value = "设备信息", required = true) @RequestBody Facility facility, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try{
                Optional<Facility> updatedFacility = facilityService.updateFacility(facility);
                if (updatedFacility.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("设备更新成功", updatedFacility.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("设备更新失败，设备不存在"));
                }}catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }

    /**
     * 根据设施名称模糊查找设施信息。
     *
     * @param name    设施名称
     * @param session HTTP 会话
     * @return 符合条件的设施列表的 ResponseEntity
     */
    @ApiOperation(value = "根据设施名字模糊查找用户", notes = "返回符合条件的设施列表，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取设施信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备更新权限")
    })
    @GetMapping("/searchFacilityByName")
    public ResponseEntity<String> findFacilityByName(
            @ApiParam(name = "name", value = "设备名称", required = true, example = "john") @RequestParam("name") String name,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查找权限"));
        }

        List<Facility> facilities = facilityService.findFacilitiesByNameLike(name);

        return ResponseEntity.ok(Result.okGetStringByData("获取设备信息成功", facilities));
    }


    /**
     * 获取所有设施信息。
     *
     * @param session HTTP 会话
     * @return 所有设施列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有设备", notes = "返回所有设备列表，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有设备信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findAllFacilities")
    public ResponseEntity<String> findAllFacilities(HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        List<Facility> facilities = facilityService.findAllFacilities();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有设备信息成功", facilities));
    }




    /**
     * 根据设备ID获取设备信息。
     *
     * @param id 设备ID
     * @param session HTTP会话
     * @return ResponseEntity 包含设备信息的响应实体
     */
    @ApiOperation(value = "根据科室id获取设备", notes = "返回对应id，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有设备信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findFacilityById")
    public ResponseEntity<String> findFacilityById(
            @ApiParam(name = "id", value = "设备id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        Optional<Facility> facility = facilityService.findFacilityById(new ObjectId(id));
        return ResponseEntity.ok(Result.okGetStringByData("获取设备信息成功", facility));
    }



}
