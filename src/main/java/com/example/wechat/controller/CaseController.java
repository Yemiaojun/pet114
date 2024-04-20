package com.example.wechat.controller;


import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Case;
import com.example.wechat.service.CaseService;
import com.example.wechat.service.FileStorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/case")

public class CaseController {


    @Autowired
    private CaseService caseService;

    @Autowired
    private FileStorageService fileStorageService;
    //添加病例
    @ApiOperation(value = "添加病例", notes = "添加新的病例，需要管理员权限")
    @PostMapping("/addCase")
    public ResponseEntity<String> addCase(
            @ApiParam(value = "病例信息", required = true) @RequestBody Case newCase,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Optional<Case> savedCase = caseService.addCase(newCase);
                return ResponseEntity.ok(Result.okGetStringByData("病例添加成功", savedCase));
            } catch (DefaultException de) {
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }

    //删除病例
    @ApiOperation(value = "删除病例", notes = "删除指定的病例，需要管理员权限")
    @DeleteMapping("/deleteCase")
    public ResponseEntity<String> deleteCase(
            @ApiParam(value = "病例信息", required = true) @RequestBody Map<String, String> payload,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String id = payload.get("id");
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                ObjectId caseId = new ObjectId(id);
                Optional<Case> deletedCase = caseService.deleteCase(caseId);
                return ResponseEntity.ok(Result.okGetStringByData("病例删除成功", deletedCase));
            } catch (DefaultException de) {
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备删除权限"));
        }
    }

    //更新病例
    @ApiOperation(value = "更新病例", notes = "更新指定的病例，需要管理员权限")
    @PutMapping ("/updateCase")
    public ResponseEntity<String> updateCase(
            @ApiParam(value = "病例信息", required = true) @RequestBody Case updatedCase,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try {
                Optional<Case> savedCase = caseService.updateCase(updatedCase);
                return ResponseEntity.ok(Result.okGetStringByData("病例更新成功", savedCase));
            } catch (DefaultException de) {
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }

    //查找所有病例
    @ApiOperation(value = "查找所有病例", notes = "查找所有病例，需要管理员权限")
    @GetMapping("/findAllCases")
    public ResponseEntity<String> findAllCases(HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");

        if (userIdStr != null) {
            Iterable<Case> allCases = caseService.findAllCases();
            return ResponseEntity.ok(Result.okGetStringByData("查找所有病例成功", allCases));
        }
        else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //通过病例id查找病例
    @ApiOperation(value = "通过病例id查找病例", notes = "通过病例id查找病例，需要管理员权限")
    @GetMapping("/findCaseById")
    public ResponseEntity<String> findCaseById(
            @ApiParam(value = "病例id", required = true) String id,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录
        if (userIdStr != null) {
            // 将字符串转换为ObjectId
            ObjectId caseId = new ObjectId(id);
            Optional<Case> foundCase = caseService.findCaseById(caseId);
            return ResponseEntity.ok(Result.okGetStringByData("通过病例id查找病例成功", foundCase));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //通过疾病id查找病例
    @ApiOperation(value = "通过疾病id查找病例", notes = "通过疾病id查找病例，需要管理员权限")
    @GetMapping("/findCaseByDiseaseId")
    public ResponseEntity<String> findCaseByDiseaseId(
            @ApiParam(value = "疾病id", required = true) @RequestBody String diseaseId,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录
        if (userIdStr != null) {
            // 将字符串转换为ObjectId
            ObjectId id = new ObjectId(diseaseId);
            Iterable<Case> cases = caseService.findCaseByDiseaseId(id);
            return ResponseEntity.ok(Result.okGetStringByData("通过疾病id查找病例成功", cases));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }


    //通过病例名称查找病例
    @ApiOperation(value = "通过病例名称查找病例", notes = "通过病例名称查找病例，需要管理员权限")
    @GetMapping("/findCaseByName")
    public ResponseEntity<String> findCaseByName(
            @ApiParam(value = "病例名称", required = true) @RequestBody String name,
            HttpSession session) {
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");
        // 确认用户已登录
        if (userIdStr != null) {
            Optional<Case> cases = caseService.findCaseByName(name);
            return ResponseEntity.ok(Result.okGetStringByData("通过病例名称查找病例成功", cases));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //通过病例描述模糊查找病例
    @ApiOperation(value = "通过病例描述模糊查找病例", notes = "通过病例描述模糊查找病例,需要用户已经登录")
    @GetMapping("/findCaseByTextListLike")
    public ResponseEntity<String> findCaseByTextListLike(
            @ApiParam(value = "病例描述", required = true) @RequestBody String regex,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            Iterable<Case> cases = caseService.findCaseByTextListLike(regex);
            return ResponseEntity.ok(Result.okGetStringByData("通过病例描述模糊查找病例成功", cases));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

   //上传一个病例的图片到图片列表的最后
    @ApiOperation(value = "上传病例图片", notes = "上传病例图片到列表的最后，需要用户已经登录")
    @PostMapping("/uploadCaseImage")
    public ResponseEntity<String> uploadCaseImageToLast(
            @ApiParam(value = "病例图片", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            String picUrl = fileStorageService.storeCasePic(file, file.getName());
            caseService.uploadCaseImageUrlToLast(id, picUrl);
            return ResponseEntity.ok(Result.okGetString("上传病例图片成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //上传一个病例的图片到指定位置
    @ApiOperation(value = "上传病例图片到指定位置", notes = "上传病例图片到指定位置，需要用户已经登录")
    @PostMapping("/uploadCaseImageToIndex")
    public ResponseEntity<String> uploadCaseImageToIndex(
            @ApiParam(value = "病例图片", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "图片位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            String picUrl = fileStorageService.storeCasePic(file, file.getName());
            caseService.uploadCaseImageUrlToIndex(id, picUrl, index);
            return ResponseEntity.ok(Result.okGetString("上传病例图片成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //批量上传病例图片到列表的最后
    @ApiOperation(value = "批量上传病例图片", notes = "批量上传病例图片到列表的最后，需要用户已经登录")
    @PostMapping("/uploadCaseImageList")
    public ResponseEntity<String> uploadCaseImageListToLast(
            @ApiParam(value = "病例图片", required = true) @RequestParam("file") MultipartFile[] files,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            for (MultipartFile file : files) {
                String picUrl = fileStorageService.storeCasePic(file, file.getName());
                caseService.uploadCaseImageUrlToLast(id, picUrl);
            }
            return ResponseEntity.ok(Result.okGetString("批量上传病例图片成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //批量上传病例图片到指定位置
    @ApiOperation(value = "批量上传病例图片到指定位置", notes = "批量上传病例图片到指定位置，需要用户已经登录")
    @PostMapping("/uploadCaseImageListToIndex")
    public ResponseEntity<String> uploadCaseImageListToIndex(
            @ApiParam(value = "病例图片", required = true) @RequestParam("file") MultipartFile[] files,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "图片位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            for (MultipartFile file : files) {
                String picUrl = fileStorageService.storeCasePic(file, file.getName());
                caseService.uploadCaseImageUrlToIndex(id, picUrl, index);
                index++;
            }
            return ResponseEntity.ok(Result.okGetString("批量上传病例图片成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //删除病例中的指定图片
    @ApiOperation(value = "删除病例图片", notes = "删除病例中的指定图片，需要用户已经登录")
    @DeleteMapping("/deleteCaseImage")
    public ResponseEntity<String> deleteCaseImage(
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "图片位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            try {
                ObjectId id = new ObjectId(caseId);
                caseService.deleteCaseImageUrl(id, index);
                return ResponseEntity.ok(Result.okGetString("删除病例图片成功"));
            } catch (DefaultException de) {
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //上传一个病例的视频到视频列表的最后
    @ApiOperation(value = "上传病例视频", notes = "上传病例视频到列表的最后，需要用户已经登录")
    @PostMapping("/uploadCaseVideo")
    public ResponseEntity<String> uploadCaseVideoToLast(
            @ApiParam(value = "病例视频", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            String videoUrl = fileStorageService.storeCaseVideo(file, file.getName());
            caseService.uploadCaseVideoUrlToLast(id, videoUrl);
            return ResponseEntity.ok(Result.okGetString("上传病例视频成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //上传一个病例的视频到指定位置
    @ApiOperation(value = "上传病例视频到指定位置", notes = "上传病例视频到指定位置，需要用户已经登录")
    @PostMapping("/uploadCaseVideoToIndex")
    public ResponseEntity<String> uploadCaseVideoToIndex(
            @ApiParam(value = "病例视频", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "视频位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            String videoUrl = fileStorageService.storeCaseVideo(file, file.getName());
            caseService.uploadCaseVideoUrlToIndex(id, videoUrl, index);
            return ResponseEntity.ok(Result.okGetString("上传病例视频成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //批量上传病例视频到列表的最后
    @ApiOperation(value = "批量上传病例视频", notes = "批量上传病例视频到列表的最后，需要用户已经登录")
    @PostMapping("/uploadCaseVideoList")
    public ResponseEntity<String> uploadCaseVideoListToLast(
            @ApiParam(value = "病例视频", required = true) @RequestParam("file") MultipartFile[] files,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            for (MultipartFile file : files) {
                String videoUrl = fileStorageService.storeCaseVideo(file, file.getName());
                caseService.uploadCaseVideoUrlToLast(id, videoUrl);
            }
            return ResponseEntity.ok(Result.okGetString("批量上传病例视频成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //批量上传病例视频到指定位置
    @ApiOperation(value = "批量上传病例视频到指定位置", notes = "批量上传病例视频到指定位置，需要用户已经登录")
    @PostMapping("/uploadCaseVideoListToIndex")
    public ResponseEntity<String> uploadCaseVideoListToIndex(
            @ApiParam(value = "病例视频", required = true) @RequestParam("file") MultipartFile[] files,
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "视频位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            ObjectId id = new ObjectId(caseId);
            for (MultipartFile file : files) {
                String videoUrl = fileStorageService.storeCaseVideo(file, file.getName());
                caseService.uploadCaseVideoUrlToIndex(id, videoUrl, index);
                index++;
            }
            return ResponseEntity.ok(Result.okGetString("批量上传病例视频成功"));
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    //删除病例中的指定视频
    @ApiOperation(value = "删除病例视频", notes = "删除病例中的指定视频，需要用户已经登录")
    @DeleteMapping("/deleteCaseVideo")
    public ResponseEntity<String> deleteCaseVideo(
            @ApiParam(value = "病例id", required = true) @RequestParam("caseId") String caseId,
            @ApiParam(value = "视频位置", required = true) @RequestParam("index") int index,
            HttpSession session) {
        // 检查会话中是否有用户ID
        String userIdStr = (String) session.getAttribute("userId");
        // 确认用户已登录
        if (userIdStr != null) {
            try {
                ObjectId id = new ObjectId(caseId);
                caseService.deleteCaseVideoUrl(id, index);
                return ResponseEntity.ok(Result.okGetString("删除病例视频成功"));
            } catch (DefaultException de) {
                return ResponseEntity.badRequest().body(Result.errorGetString(de.getMessage()));
            }
        } else {
            // 用户未登录
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }
    }

    @ApiOperation(value= "上传文件")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFiles(
                @ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
                @ApiParam(value = "病例id", required = true) @RequestParam("id") String id,
                HttpSession session
            ){
        // 检查会话中是否有用户ID和auth信息
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");



        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                caseService.uploadFile(multipartFile,id);
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
                caseService.uploadAvatar(multipartFile,id);
                return ResponseEntity.ok(Result.okGetString("上传文件成功"));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));

            }
        }
        return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或无权限"));

    }
}