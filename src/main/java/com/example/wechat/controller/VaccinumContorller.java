package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Inpatient;
import com.example.wechat.model.Vaccinum;
import com.example.wechat.service.VaccinumService;
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
@RequestMapping("/vaccinum")
public class VaccinumContorller {
    @Autowired
    private VaccinumService vaccinumService;
    /**
     * 添加新的疫苗信息。
     * @param vaccinum 疫苗信息
     * @param session    HTTP 会话
     * @return 添加成功后的疫苗信息
     */
    @ApiOperation(value = "添加疫苗", notes = "添加新的疫苗，需要管理员权限")
    @PostMapping("/addVaccinum")
    public ResponseEntity<String> addVaccinum(
            @ApiParam(value = "疫苗信息", required = true) @RequestBody Vaccinum vaccinum,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Vaccinum savedVaccinum = vaccinumService.addVaccinum(vaccinum);
                return ResponseEntity.ok(Result.okGetStringByData("疫苗添加成功", savedVaccinum));
            } catch (NameAlreadyExistedException naee) {
                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 删除疫苗信息。
     * @param payload 请求体信息，包含部门id
     * @param session HTTP 会话
     * @return 删除成功后的疫苗信息
     */
    @ApiOperation(value = "删除疫苗", notes = "删除疫苗，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "疫苗", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteVaccinum")
    public ResponseEntity<String> deleteVaccinum(
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
                Optional<Vaccinum> optionalVaccinum = vaccinumService.deleteVaccinumById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("疫苗删除成功", optionalVaccinum));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 更新疫苗信息。
     * @param vaccinum 要更新的疫苗信息
     * @param session    HTTP 会话
     * @return 更新后的疫苗信息
     */
    @ApiOperation(value = "更新疫苗信息", notes = "根据提供的疫苗信息更新疫苗，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "疫苗更新成功"),
            @ApiResponse(code = 400, message = "疫苗更新失败，疫苗可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateVaccinum")
    public ResponseEntity<String> updateVaccinum(@ApiParam(value = "疫苗信息", required = true) @RequestBody Vaccinum vaccinum, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try {
                Optional<Vaccinum> updatedVaccinum = vaccinumService.updateVaccinum(vaccinum);
                if (updatedVaccinum.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("疫苗更新成功", updatedVaccinum.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("疫苗更新失败，疫苗可能不存在"));
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
    @ApiOperation(value = "获取所有疫苗", notes = "返回所有科室疫苗，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有疫苗信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findAllVaccinums")
    public ResponseEntity<String> findAllVaccinums(HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        List<Vaccinum> vaccinums = vaccinumService.findAllVaccinums();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有疫苗信息成功", vaccinums));
    }
    /**
     * 根据疫苗ID获取部门信息。
     * @param id      疫苗ID
     * @param session HTTP会话
     * @return ResponseEntity 包含疫苗信息的响应实体
     */
    @ApiOperation(value = "根据疫苗id获取疫苗", notes = "返回对应疫苗，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取疫苗信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findVaccinumById")
    public ResponseEntity<String> findVaccinumById(
            @ApiParam(name = "id", value = "疫苗id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        try{
            Optional<Vaccinum> vaccinum = vaccinumService.findVaccinumById(id);
            return ResponseEntity.ok(Result.okGetStringByData("获取病患信息成功", vaccinum));}catch (Exception e){
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }
    }

    @ApiOperation(value= "上传文件")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFiles(
            @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
            @ApiParam(value = "疫苗id", required = true) @RequestParam("id") String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                vaccinumService.uploadFile(multipartFile,id);
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
            @ApiParam(value = "病患id", required = true) @PathVariable String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                vaccinumService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }
}
