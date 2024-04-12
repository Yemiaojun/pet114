package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Assay;
import com.example.wechat.service.AssayService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
     * 添加新的化验信息。
     * @param assay 化验信息
     * @param session    HTTP 会话
     * @return 添加成功后的化验信息
     */
    @ApiOperation(value = "添加化验", notes = "添加新的化验，需要管理员权限")
    @PostMapping("/addAssay")
    public ResponseEntity<String> addAssay(
            @ApiParam(value = "化验信息", required = true) @RequestBody Assay assay,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Assay savedAssay = assayService.addAssay(assay);
                return ResponseEntity.ok(Result.okGetStringByData("化验信息添加成功", savedAssay));
            } catch (Exception naee) {
                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 删除信息。
     * @param payload 请求体信息，包含化验id
     * @param session HTTP 会话
     * @return 删除成功后的化验信息
     */
    @ApiOperation(value = "删除化验信息", notes = "删除化验信息，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "化验", required = true, dataType = "String", paramType = "query")
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
                return ResponseEntity.ok(Result.okGetStringByData("化验删除成功", optionalAssay));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 更新化验信息。
     * @param assay 要更新的化验信息
     * @param session    HTTP 会话
     * @return 更新后的化验信息
     */
    @ApiOperation(value = "更新化验信息", notes = "根据提供的化验信息更新化验信息，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "化验更新成功"),
            @ApiResponse(code = 400, message = "化验更新失败，化验可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateAssay")
    public ResponseEntity<String> updateAssay(@ApiParam(value = "化验信息", required = true) @RequestBody Assay assay, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try {
                Optional<Assay> updatedAssay = assayService.updateAssay(assay);
                if (updatedAssay.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("化验信息更新成功", updatedAssay.get()));
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
     * 获取所有化验信息。
     * @param session HTTP 会话
     * @return 所有部门列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有化验信息", notes = "返回所有化验列表，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有化验信息成功"),
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
     * 根据化验ID获取部门信息。
     * @param id      化验ID
     * @param session HTTP会话
     * @return ResponseEntity 包含化验信息的响应实体
     */
    @ApiOperation(value = "根据化验id获取化验", notes = "返回对应化验，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取化验信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findAssayById")
    public ResponseEntity<String> findAssayById(
            @ApiParam(name = "id", value = "化验id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        try{
        Optional<Assay> assay = assayService.findAssayById(id);
        return ResponseEntity.ok(Result.okGetStringByData("获取部门信息成功", assay));}catch (Exception e){
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

        }

    }

    @ApiOperation(value= "上传文件")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFiles(
            @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
            @ApiParam(value = "化验单id", required = true) @RequestParam("id") String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                assayService.uploadFile(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }



    @ApiOperation(value= "上传头像")
    @PostMapping("/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(
            @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
            @ApiParam(value = "化验单id", required = true) @PathVariable String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                assayService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }
}
