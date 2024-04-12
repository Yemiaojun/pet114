package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Facility;
import com.example.wechat.model.Procedure;
import com.example.wechat.service.DepartmentService;
import com.example.wechat.service.FileStorageService;
import com.example.wechat.service.ProcedureService;
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
@RequestMapping("/procedure")
public class ProcedureController {

    @Autowired
    private ProcedureService procedureService;

    @Autowired
    private FileStorageService fileStorageService;

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
    @ApiImplicitParam(name = "id", value = "流程，不能为空", required = true, dataType = "String", paramType = "query" )
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
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }

        List<Procedure> procedures = procedureService.findProcedureByRoleId(new ObjectId(id));
        return ResponseEntity.ok(Result.okGetStringByData("获取部流程息成功", procedures));
    }

    /**
     * 上传流程资源。
     *
     * @param file     流程资源文件
     * @param id       流程id
     * @param type     资源类型（图片或视频）
     * @param session  HTTP 会话
     * @return 包含更新结果的 ResponseEntity
     */
    @ApiOperation(value = "上传流程资源", notes = "上传流程资源，需要管理员权限")
    @PostMapping("/uploadResource")
    public ResponseEntity<String> uploadResource(
            @ApiParam(value = "流程资源文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "流程id", required = true) @RequestParam String id,
            @ApiParam(value = "资源类型（pic 或 vid）", required = true) @RequestParam String type,
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
            ObjectId procedureId = new ObjectId(id);
            Procedure procedure = procedureService.findProcedureById(procedureId).orElseThrow(() -> new RuntimeException("过程不存在"));
            String filePath;
            if ("pic".equalsIgnoreCase(type)) {
                filePath = fileStorageService.storeProcedurePic(file, file.getOriginalFilename());
                procedureService.updateProcedurePicUrl(procedureId, filePath);
                return ResponseEntity.ok(Result.okGetStringByData("图片更新成功", filePath));
            } else if ("vid".equalsIgnoreCase(type)) {
                filePath = fileStorageService.storeProcedureVid(file, file.getOriginalFilename());
                procedureService.uploadProcedureVidUrl(procedureId, filePath);
                return ResponseEntity.ok(Result.okGetStringByData("视频更新成功", filePath));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("不支持的资源类型"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("资源更新失败: " + e.getMessage()));
        }
    }


    /**
     * 删除流程资源。
     *
     * @param name     流程资源文件名
     * @param id       流程id
     * @param type     资源类型（图片或视频）
     * @param session  HTTP 会话
     * @return 包含更新结果的 ResponseEntity
     */
    @ApiOperation(value = "删除流程资源", notes = "删除流程资源，需要管理员权限")
    @PostMapping("/deleteResource")
    public ResponseEntity<String> deleteResource(
            @ApiParam(value = "流程资源文件名", required = true) @RequestParam String name,
            @ApiParam(value = "流程id", required = true) @RequestParam String id,
            @ApiParam(value = "资源类型（pic 或 vid）", required = true) @RequestParam String type,
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
            ObjectId procedureId = new ObjectId(id);
            Procedure procedure = procedureService.findProcedureById(procedureId).orElseThrow(() -> new RuntimeException("设备不存在"));
            String filePath;
            if ("pic".equalsIgnoreCase(type)) {
                fileStorageService.deleteProcedurePic(String.valueOf(procedure.getId()), name);
                procedureService.deleteProcedurePicUrl(procedureId, name);
                return ResponseEntity.ok(Result.okGetStringByData("图片删除成功",name));
            } else if ("vid".equalsIgnoreCase(type)) {
                fileStorageService.deleteProcedureVid(String.valueOf(procedure.getId()), name);
                procedureService.deleteProcedureVidUrl(procedureId, name);
                return ResponseEntity.ok(Result.okGetStringByData("视频删除成功",name));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("不支持的资源类型"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("资源更新失败: " + e.getMessage()));
        }
    }


    @ApiOperation(value= "上传文件")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFiles(
            @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
            @ApiParam(value = "步骤id", required = true) @RequestParam("id") String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                procedureService.uploadFile(multipartFile,id);
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
            @ApiParam(value = "步骤id", required = true) @RequestParam("id") String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                procedureService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }

}
