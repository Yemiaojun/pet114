package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Charge;
import com.example.wechat.service.ChargeService;
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
@RequestMapping("/charge")
public class ChargeController {

    @Autowired
    private ChargeService chargeService;
    /**
     * 添加新的收据信息。
     * @param charge 收据信息
     * @param session    HTTP 会话
     * @return 添加成功后的收据信息
     */
    @ApiOperation(value = "添加收据", notes = "添加新的收据，需要管理员权限")
    @PostMapping("/addCharge")
    public ResponseEntity<String> addCharge(
            @ApiParam(value = "科室信息", required = true) @RequestBody Charge charge,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Charge savedCharge = chargeService.addCharge(charge);
                return ResponseEntity.ok(Result.okGetStringByData("收据添加成功", savedCharge));
            } catch (NameAlreadyExistedException naee) {
                return ResponseEntity.badRequest().body(Result.errorGetString(naee.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 删除收据信息。
     * @param payload 请求体信息，包含收据id
     * @param session HTTP 会话
     * @return 删除成功后的收据信息
     */
    @ApiOperation(value = "删除收据", notes = "删除收据，需要管理员权限")
    @ApiImplicitParam(name = "id", value = "收据", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteCharge")
    public ResponseEntity<String> deleteCharge(
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
                Optional<Charge> optionalCharge = chargeService.deleteChargeById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("收据删除成功", optionalCharge));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }
    /**
     * 更新收据信息。
     * @param charge 要更新的收据信息
     * @param session    HTTP 会话
     * @return 更新后的收据信息
     */
    @ApiOperation(value = "更新收据信息", notes = "根据提供的收据信息更新收据，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "收据更新成功"),
            @ApiResponse(code = 400, message = "收据更新失败，收据可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateCharge")
    public ResponseEntity<String> updateCharge(@ApiParam(value = "收据信息", required = true) @RequestBody Charge charge, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try {
                Optional<Charge> updatedCharge = chargeService.updateCharge(charge);
                if (updatedCharge.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("收据更新成功", updatedCharge.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("收据更新失败，收据可能不存在"));
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
     * 获取所有收据信息。
     * @param session HTTP 会话
     * @return 所有收据列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有收据", notes = "返回所有收据列表，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有收据信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findAllCharges")
    public ResponseEntity<String> findAllCharges(HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        List<Charge> charges = chargeService.findAllCharges();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有收据信息成功", charges));
    }
    /**
     * 根据收据ID获取部门信息。
     * @param id      收据ID
     * @param session HTTP会话
     * @return ResponseEntity 包含部门信息的响应实体
     */
    @ApiOperation(value = "根据收据id获取收据", notes = "返回对应收据，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取收据信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findChargeById")
    public ResponseEntity<String> findChargeById(
            @ApiParam(name = "id", value = "收据id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        try{
        Optional<Charge> charge = chargeService.findChargeById(id);
        return ResponseEntity.ok(Result.okGetStringByData("获取部门信息成功", charge));}catch (Exception e){
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }

    }

    @ApiOperation(value= "上传文件")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFiles(
            @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
            @ApiParam(value = "收据单id", required = true) @RequestParam("id") String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                chargeService.uploadFile(multipartFile,id);
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
            @ApiParam(value = "病例id", required = true) @PathVariable String id,
            HttpSession session
    ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                chargeService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }
}
