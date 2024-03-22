package com.example.wechat.controller;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.service.DepartmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    /**
     * 添加新的科室信息。
     *
     * @param department 科室信息
     * @param session HTTP 会话
     * @return 添加成功后的科室信息
     */
    @ApiOperation(value="添加科室", notes = "添加新的科室，需要管理员权限")
    @PostMapping("/adddepartment")
    public ResponseEntity<String> addDepartment(
            @ApiParam(value = "疾病信息", required = true) @RequestBody Department department,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Department savedDepartment = departmentService.addDepartment(department);
                return ResponseEntity.ok(Result.okGetStringByData("科室添加成功", savedDepartment));
            }catch (NameAlreadyExistedException naee){

                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }


    /**
     * 删除部门信息。
     *
     * @param name    要删除的部门名称
     * @param session HTTP 会话
     * @return 删除成功后的部门信息
     */
    @ApiOperation(value="删除部门", notes = "删除部门，需要管理员权限")
    @DeleteMapping ("/deleteDeparment")
    public ResponseEntity<String> deleteDepartment(
            @ApiParam(value = "科室名字", required = true) @RequestBody String name,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Optional<Department> optionalDepartment = departmentService.deleteDepartmentByName(name);
                return ResponseEntity.ok(Result.okGetStringByData("科室删除成功",optionalDepartment));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }


    /**
     * 更新科室信息。
     *
     * @param department 要更新的疾病信息
     * @param session HTTP 会话
     * @return 更新后的疾病信息
     */
    @ApiOperation(value = "更新科室信息", notes = "根据提供的科室信息更新科室，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "科室更新成功"),
            @ApiResponse(code = 400, message = "科室更新失败，科室可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateDepartment")
    public ResponseEntity<String> updateDepartment(@ApiParam(value = "科室信息", required = true) @RequestBody Department department, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try{
                Optional<Department> updatedDepartment = departmentService.updateDepartment(department);
                if (updatedDepartment.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("科室更新成功", updatedDepartment.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("科室更新失败，科室可能不存在"));
                }}catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }

}
