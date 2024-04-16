package com.example.wechat.controller;

import com.example.wechat.model.Activity;
import com.example.wechat.model.Assay;
import com.example.wechat.model.Procedure;
import com.example.wechat.model.Role;
import com.example.wechat.service.ActivityService;
import com.example.wechat.service.AssayService;
import com.example.wechat.service.FileStorageService;
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
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 添加新的角色活动信息。
     * @param activity 角色活动信息
     * @param session    HTTP 会话
     * @return 添加成功后的角色活动信息
     */
    @ApiOperation(value = "添加角色活动", notes = "添加新的角色活动，需要管理员权限")
    @PostMapping("/addActivity")
    public ResponseEntity<String> addActivity(
            @ApiParam(value = "角色活动信息", required = true) @RequestBody Activity activity,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Activity savedActivity = activityService.addActivity(activity);
                return ResponseEntity.ok(Result.okGetStringByData("角色活动信息添加成功", savedActivity));
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
     * @return 删除成功后的角色活动信息
     */
    @ApiOperation(value = "删除角色活动信息", notes = "删除角色活动信息，需要管理员权限")
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
                Optional<Activity> optionalActivity = activityService.deleteActivityById(new ObjectId(id));
                return ResponseEntity.ok(Result.okGetStringByData("角色活动删除成功", optionalActivity));
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
     * @param activity 要更新的角色活动信息
     * @param session    HTTP 会话
     * @return 更新后的化验信息
     */
    @ApiOperation(value = "更新角色活动信息", notes = "根据提供的角色活动信息更新角色活动信息，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "角色活动更新成功"),
            @ApiResponse(code = 400, message = "角色活动更新失败，化验可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateActivity")
    public ResponseEntity<String> updateActivity(@ApiParam(value = "角色活动信息", required = true) @RequestBody Activity activity, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            try {
                Optional<Activity> updatedActivity = activityService.updateActivity(activity);
                if (updatedActivity.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("化验信息更新成功", updatedActivity.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("角色活动更新失败，科室可能不存在"));
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
     * 获取所有角色活动信息。
     * @param session HTTP 会话
     * @return 所有部门列表的 ResponseEntity
     */
    @ApiOperation(value = "获取所有角色活动信息", notes = "返回所有角色活动列表，需要用户登录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有角色活动信息成功"),
            @ApiResponse(code = 400, message = "用户未登录")
    })
    @GetMapping("/findAllActivities")
    public ResponseEntity<String> findAllActivities(HttpSession session) {
        // 检查用户登录
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
        List<Activity> activities = activityService.findAllActivities();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有角色活动信息成功", activities));
    }

    /**
     * 根据角色name获取活动信息。
     *
     * @param name 角色name
     * @param session HTTP会话
     * @return ResponseEntity 包含角色信息的响应实体
     */
    @ApiOperation(value = "根据角色id获取活动", notes = "返回对应id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有活动信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findActivityByName")
    public ResponseEntity<String> findActivityByName(
            @ApiParam(name = "id", value = "角色name", required = true, example = "saisunwoiudoiu") @RequestParam("name") String name,
            HttpSession session) {
        // 检查用户权限
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        Optional<Activity> activity = activityService.findActivityByName(name);
        return ResponseEntity.ok(Result.okGetStringByData("获取活动信息成功", activity));
    }


    /**
     * 根据角色id获取活动信息。
     *
     * @param id 角色id
     * @param session HTTP会话
     * @return ResponseEntity 包含角色信息的响应实体
     */
    @ApiOperation(value = "根据角色id获取活动", notes = "返回对应id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有活动信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findActivitiesByRoleId")
    public ResponseEntity<String> findActivitiesByRoleId(
            @ApiParam(name = "id", value = "角色id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id,
            HttpSession session) {
        // 检查用户权限
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        List<Activity> activity = activityService.findActivityByRoleId(id);
        return ResponseEntity.ok(Result.okGetStringByData("获取活动信息成功", activity));
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
                activityService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }

    /**
     * 上传流程资源。
     *
     * @param file     活动资源文件
     * @param id       活动id
     * @param type     资源类型（图片或视频）
     * @param session  HTTP 会话
     * @return 包含更新结果的 ResponseEntity
     */
    @ApiOperation(value = "上传活动资源", notes = "上传活动资源，需要管理员权限")
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
            Activity activity = activityService.findActivityById(id).orElseThrow(() -> new RuntimeException("过程不存在"));
            String filePath;
            if ("pic".equalsIgnoreCase(type)) {
                filePath = fileStorageService.storeProcedurePic(file, file.getOriginalFilename());
                activityService.updateActivityPicUrl(new ObjectId(id), filePath);
                return ResponseEntity.ok(Result.okGetStringByData("图片更新成功", filePath));
            } else if ("vid".equalsIgnoreCase(type)) {
                filePath = fileStorageService.storeProcedureVid(file, id);
                activityService.updateActivityVidUrl(new ObjectId(id), filePath);
                return ResponseEntity.ok(Result.okGetStringByData("视频更新成功", filePath));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("不支持的资源类型"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("资源更新失败: " + e.getMessage()));
        }
    }


}
