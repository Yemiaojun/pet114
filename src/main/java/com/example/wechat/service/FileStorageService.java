package com.example.wechat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${file.avatar-dir}")
    private String avatarDir;

    @Value("file/facilitypic/")
    private String facilityPicDir;

    @Value("file/casepic/")
    private String casePicDir;

    public String storeAvatar(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(avatarDir + username + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    public String storeFacilityPic(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(facilityPicDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    //存储病例的图片
    public String storeCasePic(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(casePicDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    //存储病例的视频
    public String storeCaseVideo(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(casePicDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }


}
