package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Assay;
import com.example.wechat.service.AssayService;
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
@RequestMapping("/assay")
public class AssayController {
    @Autowired
    private AssayService assayService;
    /**
     * 添加新的科室信息。
     * @param assay 科室信息
     * @param session    HTTP 会话
     * @return 添加成功后的科室信息
     */
    @ApiOperation(value = "添加科室", notes = "添加新的科室，需要管理员权限")
    @PostMapping("/addAssay")
    public ResponseEntity<String> addAssay(
            @ApiParam(value = "科室信息", required = true) @RequestBody Assay assay,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Assay savedAssay = assayService.addAssay(assay);
                return ResponseEntity.ok(Result.okGetStringByData("科室添加成功", savedAssay));
            } catch (NameAlreadyExistedException naee) {
                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 删除部门信息。
     * @param payload 请求体信息，包含部门id
     * @param session HTTP 会话
     * @return 删除成功后的部门信息
     */
    @ApiOperation(value = "删除部门", notes = "删除部门，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "部门", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteAssay")
    public ResponseEntity<String> deleteAssay(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 从请求体中获取部门id
        String id = payload.get("id");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Optional<Assay> optionalAssay = assayService.deleteAssayById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("科室删除成功", optionalAssay));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 更新科室信息。
     * @param assay 要更新的疾病信息
     * @param session    HTTP 会话
     * @return 更新后的疾病信息
     */
    @ApiOperation(value = "更新科室信息", notes = "根据提供的科室信息更新科室，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "科室更新成功"),
            @ApiResponse(code = 400, message = "科室更新失败，科室可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateAssay")
    public ResponseEntity<String> updateAssay(@ApiParam(value = "科室信息", required = true) @RequestBody Assay assay, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try {
                Optional<Assay> updatedAssay = assayService.updateAssay(assay);
                if (updatedAssay.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("科室更新成功", updatedAssay.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("科室更新失败，科室可能不存在"));
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }
    /**
     * 获取所有部门信息。
     * @param session HTTP 会话
     * @return 所有部门列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有科室", notes = "返回所有科室列表，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有科室信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findAllAssays")
    public ResponseEntity<String> findAllAssays(HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        List<Assay> assays = assayService.findAllAssays();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有科室信息成功", assays));
    }
    /**
     * 根据部门ID获取部门信息。
     * @param id      部门ID
     * @param session HTTP会话
     * @return ResponseEntity 包含部门信息的响应实体
     */
    @ApiOperation(value = "根据部门id获取部门", notes = "返回对应部门，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取部门信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findAssayById")
    public ResponseEntity<String> findAssayById(
            @ApiParam(name = "id", value = "部门id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        // Optional<Assay> assay = assayService.findAssayById(new ObjectId(id));
        // return ResponseEntity.ok(Result.okGetStringByData("获取部门信息成功", assay));
        return null;
    }
}
