package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Facility;
import com.example.wechat.model.Role;
import com.example.wechat.service.RoleService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;


    /**
     * 添加新的角色信息。
     *
     * @param role 角色信息
     * @param session HTTP 会话
     * @return 添加成功后的角色信息
     */
    @ApiOperation(value="添加角色", notes = "添加新的角色，需要管理员权限")
    @PostMapping("/addRole")
    public ResponseEntity<String> addRole(
            @ApiParam(value = "角色信息", required = true) @RequestBody Role role,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Role savedRole = roleService.addRole(role);
                return ResponseEntity.ok(Result.okGetStringByData("角色添加成功", savedRole));
            }catch (NameAlreadyExistedException naee){

                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));

            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }

    }

    /**
     * 删除角色信息。
     *
     * @param payload    请求体，其中包含角色id
     * @param session HTTP 会话
     * @return 删除成功后的角色信息
     */
    @ApiOperation(value="删除角色", notes = "删除角色，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "角色", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteRole")
    public ResponseEntity<String> deleteRole(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 从请求体中获取角色id
        String id = payload.get("id");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Optional<Role> optionalRole = roleService.deleteRoleById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("角色删除成功",optionalRole));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }

        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }


    /**
     * 更新角色信息。
     *
     * @param role 要更新的角色信息
     * @param session HTTP 会话
     * @return 更新后的设备信息
     */
    @ApiOperation(value = "更新角色信息", notes = "根据提供的角色信息更新角色，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "角色更新成功"),
            @ApiResponse(code = 400, message = "角色更新失败，角色可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateRole")
    public ResponseEntity<String> updateRole(@ApiParam(value = "角色信息", required = true) @RequestBody Role role, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try{
                Optional<Role> updatedRole = roleService.updateRole(role);
                if (updatedRole.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("角色更新成功", updatedRole.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("角色更新失败，角色不存在"));
                }}catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }


    /**
     * 获取所有角色信息。
     *
     * @param session HTTP 会话
     * @return 所有角色列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有角色", notes = "返回所有角色列表，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有角色信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findAllRoles")
    public ResponseEntity<String> findAllRoles(HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        List<Role> roles = roleService.findAllRoles();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有角色信息成功", roles));
    }




    /**
     * 根据角色ID获取设备信息。
     *
     * @param id 角色ID
     * @param session HTTP会话
     * @return ResponseEntity 包含角色信息的响应实体
     */
    @ApiOperation(value = "根据角色id获取设备", notes = "返回对应id，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有角色信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findRoleById")
    public ResponseEntity<String> findRoleById(
            @ApiParam(name = "id", value = "角色id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        Optional<Role> role = roleService.findRoleById(new ObjectId(id));
        return ResponseEntity.ok(Result.okGetStringByData("获取角色信息成功", role));
    }
}
