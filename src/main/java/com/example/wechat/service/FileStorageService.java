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

    public String storeAvatar(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(avatarDir + username + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }
}
