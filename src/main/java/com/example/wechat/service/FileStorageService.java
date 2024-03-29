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

    @Value("file/procedurepic/")
    private String procedurePicDir;

    @Value("file/procedurevid")
    private String procedureVidDir;
    public String storeAvatar(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(avatarDir + username + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    /**
     * 存储设施图片到指定目录，并返回存储的文件路径。
     *
     * @param file 要存储的图片文件
     * @param name 存储文件的名称前缀
     * @return 存储的文件路径
     * @throws IOException 如果存储失败或文件为空，则抛出 IOException
     */
    public String storeFacilityPic(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(facilityPicDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    /**
     * 存储存储过程图片到指定目录，并返回存储的文件路径。
     *
     * @param file 要存储的图片文件
     * @param name 存储文件的名称前缀
     * @return 存储的文件路径
     * @throws IOException 如果存储失败或文件为空，则抛出 IOException
     */
    public String storeProcedurePic(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(procedurePicDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    /**
     * 存储存储过程视频到指定目录，并返回存储的文件路径。
     *
     * @param file 要存储的视频文件
     * @param name 存储文件的名称前缀
     * @return 存储的文件路径
     * @throws IOException 如果存储失败或文件为空，则抛出 IOException
     */
    public String storeProcedureVid(MultipartFile file, String name) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get(procedureVidDir + name + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), destinationPath);

        return destinationPath.toString();
    }

    /**
     * 删除指定存储过程视频文件。
     *
     * @param procedureName 存储过程的名称
     * @param fileName      要删除的文件名称
     * @throws IOException 如果删除失败或文件不存在，则抛出 IOException
     */
    public void deleteProcedureVid(String procedureName, String fileName) throws IOException {
        Path destinationPath = Paths.get(procedureVidDir + procedureName + "_" + fileName);
        if (Files.exists(destinationPath)) {
            Files.delete(destinationPath);
            System.out.println("视频删除成功");
        } else {
            throw new IOException("要删除的视频不存在");
        }
    }

    /**
     * 删除指定存储过程图片文件。
     *
     * @param procedureName 存储过程的名称
     * @param fileName      要删除的文件名称
     * @throws IOException 如果删除失败或文件不存在，则抛出 IOException
     */
    public void deleteProcedurePic(String procedureName, String fileName) throws IOException {
        Path destinationPath = Paths.get(procedurePicDir + procedureName + "_" + fileName);
        if (Files.exists(destinationPath)) {
            Files.delete(destinationPath);
            System.out.println("图片删除成功");
        } else {
            throw new IOException("要删除的图片不存在");
        }
    }


    public String storeCasePic(MultipartFile file, String name) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get("file/casepic/" + name + "_" + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), destinationPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        return destinationPath.toString();
    }

    public void deleteCasePic(String caseName, String fileName) {
        Path destinationPath = Paths.get("file/casepic/" + caseName + "_" + fileName);
        try {
            Files.delete(destinationPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file.", e);
        }
    }

    public String storeDrugPic(MultipartFile file, String name) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get("file/drugpic/" + name + "_" + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), destinationPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        return destinationPath.toString();
    }

    public String storeCaseVideo(MultipartFile file, String name) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        Path destinationPath = Paths.get("file/casevideo/" + name + "_" + file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), destinationPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        return destinationPath.toString();
    }
}
