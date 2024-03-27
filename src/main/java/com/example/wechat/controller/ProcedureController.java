package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Procedure;
import com.example.wechat.service.DepartmentService;
import com.example.wechat.service.ProcedureService;
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
@RequestMapping("/procedure")
public class ProcedureController {

    @Autowired
    private ProcedureService procedureService;

    /**
     * 添加新的流程信息。
     *
     * @param procedure 流程信息
     * @param session HTTP 会话
     * @return 添加成功后的流程信息
     */
    @ApiOperation(value="添加流程", notes = "添加新的流程，需要管理员权限")
    @PostMapping("/addProcedure")
    public ResponseEntity<String> addProcedure(
            @ApiParam(value = "流程信息", required = true) @RequestBody Procedure procedure,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Procedure savedProcedure = procedureService.addProcedure(procedure);
                return ResponseEntity.ok(Result.okGetStringByData("流程添加成功", procedure));
            }catch (Exception naee){

                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }


    /**
     * 删除流程信息。
     *
     * @param payload    请求体信息，包含流程id
     * @param session HTTP 会话
     * @return 删除成功后的流程信息
     */
    @ApiOperation(value="删除流程", notes = "删除流程，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "流程", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteProcedure")
    public ResponseEntity<String> deleteProcedure(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");


        // 从请求体中获取流程id
        String id = payload.get("id");


        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Optional<Procedure> optionalProcedure = procedureService.deleteProcedureById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("流程删除成功",optionalProcedure));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }


    /**
     * 更新流程信息。
     *
     * @param procedure 要更新的流程信息
     * @param session HTTP 会话
     * @return 更新后的疾病信息
     */
    @ApiOperation(value = "更新流程信息", notes = "根据提供的流程信息更新流程，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "流程更新成功"),
            @ApiResponse(code = 400, message = "流程更新失败，流程可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateProcedure")
    public ResponseEntity<String> updateProcedure(@ApiParam(value = "流程信息", required = true) @RequestBody Procedure procedure, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try{
                Optional<Procedure> updatedProcedure = procedureService.updateProcedure(procedure);
                if (updatedProcedure.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("流程更新成功", updatedProcedure.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("流程更新失败，流程可能不存在"));
                }}catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }





    /**
     * 根据角色ID获取所有流程。
     *
     * @param id 角色ID
     * @param session HTTP会话
     * @return ResponseEntity 包含流程列表信息的响应实体
     */
    @ApiOperation(value = "根据角色id获取流程", notes = "返回对应流程列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有设备信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或角色id不存在")
    })
    @GetMapping("/findProceduresByRoleId")
    public ResponseEntity<String> findProceduresByRoleId(
            @ApiParam(name = "id", value = "角色id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        List<Procedure> procedures = procedureService.findProcedureByRoleId(new ObjectId(id));
        return ResponseEntity.ok(Result.okGetStringByData("获取部流程息成功", procedures));
    }

}
