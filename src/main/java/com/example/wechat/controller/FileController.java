package com.example.wechat.controller;


import com.example.wechat.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;



    @ApiOperation(value = "上传文件", notes = "上传新的文件，需要管理员权限")
    @PostMapping(path = "/uploadFile")
    public ResponseEntity<String> uploadFile
            (@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile multipartFile,
             HttpSession session){

        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
        try{
            String id = fileService.uploadFile(multipartFile);
            return ResponseEntity.ok(Result.okGetStringByData("文件上传成功成功", Optional.of(id)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }
    }else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备添加权限"));
        }
    }

    @ApiOperation(value = "根据文件id查找文件", notes = "返回符合条件的文件")
    @GetMapping(path = "/findFileById")
    public ResponseEntity<ByteArrayResource> displayFile(
            @ApiParam(name = "id", value = "图片id", required = true, example = "saisunwoiudoiu") @RequestParam("id") String id)
            throws IOException {
        var fileResponse = fileService.displayFile(id);
        var bytes = fileResponse.getBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileResponse.getFileName() + "\"")
                .body(new ByteArrayResource(bytes));
    }

    @ApiOperation(value = "根据文件id下载文件", notes = "下载符合条件的文件")
    @GetMapping(path = "/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @ApiParam(name = "id", value = "图片id", required = true) @PathVariable String id)
            throws IOException {

        var fileResponse = fileService.displayFile(id);
        var bytes = fileResponse.getBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                .body(new ByteArrayResource(bytes));
    }
}