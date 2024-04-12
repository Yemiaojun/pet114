package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Case;
import com.example.wechat.model.File;
import com.example.wechat.repository.CaseRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {


    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private FileService fileService;

    // 添加病例的业务逻辑
    public Optional<Case> addCase(Case newCase) {
        // 检查病例名称是否为空
        if(newCase.getName() == null || newCase.getName().equals("")){
            throw new DefaultException("病例名称不能为空");
        }

        // 检查是否已经存在相同名称的病例
        Optional<Case> existingCase = caseRepository.findByName(newCase.getName());
        if (existingCase.isPresent()) {
            // 病例名称已存在，返回空Optional作为错误指示
            throw new DefaultException("本病例已存在");
        }

        // 检查病例名称长度是否小于20
        if(newCase.getName().length() > 20){
            throw new DefaultException("病例名称长度不能超过20");
        }

        // 检查病例名称是否是汉语或者英语
        if(!newCase.getName().matches("[a-zA-Z0-9\\u4e00-\\u9fa5]+")){
            throw new DefaultException("病例名称只能包含汉字、字母和数字");
        }

        Case savedCase = caseRepository.save(newCase);
        return Optional.of(savedCase);
    }

    // 删除病例的业务逻辑
    public Optional<Case> deleteCase(ObjectId caseId) {
        Optional<Case> existingCase = caseRepository.findById((caseId));

        // 对应名字的case不存在则报错
        if (!existingCase.isPresent()) {
            // id不存在，返回空Optional作为错误指示
            throw new DefaultException("对应病例不存在");
        }
        caseRepository.delete(existingCase.get());
        return existingCase;
    }

    /* 更新病例的业务逻辑
      @throws IdNotFoundException    如果指定的病例 ID 不存在，则抛出该异常
     */
    public Optional<Case> updateCase(Case updatedCase) {

        // 检查病例是否存在
        Optional<Case> existingCase = caseRepository.findById(updatedCase.getId());
        if (!existingCase.isPresent()) {
            throw new DefaultException("病例不存在");
        }

        // 检查病例名称是否为空
        if(updatedCase.getName() == null || updatedCase.getName().equals("")){
            throw new DefaultException("病例名称不能为空");
        }

        // 检查病例名称长度是否小于20
        if(updatedCase.getName().length() > 20){
            throw new DefaultException("病例名称长度不能超过20");
        }

        // 检查病例名称是否是汉语或者英语
        if(!updatedCase.getName().matches("[a-zA-Z0-9\\u4e00-\\u9fa5]+")){
            throw new DefaultException("病例名称只能包含汉字、字母和数字");
        }


        // 病例存在，执行更新操作
        Case savedCase = caseRepository.save(updatedCase);
        return Optional.of(savedCase);
    }

    // 查找所有病例的业务逻辑
    public Iterable<Case> findAllCases() {
        return caseRepository.findAll();
    }

    // 根据病例名称查找病例的业务逻辑
    public Optional<Case> findCaseByName(String name) {
        return caseRepository.findByName(name);
    }

    // 根据病例ID查找病例的业务逻辑
    public Optional<Case> findCaseById(ObjectId id) {
        return caseRepository.findById(id);
    }

    // 根据病例描述模糊查找病例的业务逻辑
    public List<Case> findCaseByTextListLike(String regex) {
        return caseRepository.findByTextListLike(regex);
    }

    //通过疾病id查询病例
    public List<Case> findCaseByDiseaseId(ObjectId diseaseId) {
        return caseRepository.findByDiseaseId(diseaseId);
    }


    //上传一个病例的图片到list的最后位置
    public Optional<Case> uploadCaseImageUrlToLast(ObjectId caseId, String imageUrl) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> imageList = caseEntity.getPicUrlList();
            imageList.add(imageUrl);
            caseEntity.setPicUrlList(imageList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //上传一个病例的图片到list的指定位置
    public Optional<Case> uploadCaseImageUrlToIndex(ObjectId caseId, String imageUrl, int index) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> imageList = caseEntity.getPicUrlList();
            if(index < 0 || index > imageList.size()){
                throw new DefaultException("index超出范围");
            }
            imageList.add(index, imageUrl);
            caseEntity.setPicUrlList(imageList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }


    //批量上传病例图片到list的最后位置
    public Optional<Case> uploadCaseImageUrlListToLast(ObjectId caseId, List<String> imageUrlList) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> imageList = caseEntity.getPicUrlList();
            imageList.addAll(imageUrlList);
            caseEntity.setPicUrlList(imageList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //批量上传病例图片到list的指定位置
    public Optional<Case> uploadCaseImageUrlListToIndex(ObjectId caseId, List<String> imageUrlList, int index) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> imageList = caseEntity.getPicUrlList();
            if(index < 0 || index > imageList.size()){
                throw new DefaultException("index超出范围");
            }
            imageList.addAll(index, imageUrlList);
            caseEntity.setPicUrlList(imageList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //删除病例图片list中的指定图片
    public Optional<Case> deleteCaseImageUrl(ObjectId caseId, int index) {
            Optional<Case> existingCase = caseRepository.findById(caseId);
                if (!existingCase.isPresent()) {
                    throw new DefaultException("病例不存在");
                }
                Case caseEntity = existingCase.get();
                List<String> imageList = caseEntity.getPicUrlList();
                if(index < 0 || index >= imageList.size()){
                    throw new DefaultException("index超出范围");
                }
                imageList.remove(index);
                caseEntity.setPicUrlList(imageList);
                Case savedCase = caseRepository.save(caseEntity);
                return Optional.of(savedCase);
    }

    //上传一个病例的视频到list的最后位置
    public Optional<Case> uploadCaseVideoUrlToLast(ObjectId caseId, String videoUrl) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> videoList = caseEntity.getVideoUrlList();
            videoList.add(videoUrl);
            caseEntity.setVideoUrlList(videoList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //上传一个病例的视频到list的指定位置
    public Optional<Case> uploadCaseVideoUrlToIndex(ObjectId caseId, String videoUrl, int index) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> videoList = caseEntity.getVideoUrlList();
            if(index < 0 || index > videoList.size()){
                throw new DefaultException("index超出范围");
            }
            videoList.add(index, videoUrl);
            caseEntity.setVideoUrlList(videoList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //批量上传病例视频到list的最后位置
    public Optional<Case> uploadCaseVideoUrlListToLast(ObjectId caseId, List<String> videoUrlList) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> videoList = caseEntity.getVideoUrlList();
            videoList.addAll(videoUrlList);
            caseEntity.setVideoUrlList(videoList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //批量上传病例视频到list的指定位置
    public Optional<Case> uploadCaseVideoUrlListToIndex(ObjectId caseId, List<String> videoUrlList, int index) {
          Optional<Case> existingCase = caseRepository.findById(caseId);
            if (!existingCase.isPresent()) {
                throw new DefaultException("病例不存在");
            }
            Case caseEntity = existingCase.get();
            List<String> videoList = caseEntity.getVideoUrlList();
            if(index < 0 || index > videoList.size()){
                throw new DefaultException("index超出范围");
            }
            videoList.addAll(index, videoUrlList);
            caseEntity.setVideoUrlList(videoList);
            Case savedCase = caseRepository.save(caseEntity);
            return Optional.of(savedCase);
        }

    //删除病例视频list中的指定视频
    public Optional<Case> deleteCaseVideoUrl(ObjectId caseId, int index) {
            Optional<Case> existingCase = caseRepository.findById(caseId);
                if (!existingCase.isPresent()) {
                    throw new DefaultException("病例不存在");
                }
                Case caseEntity = existingCase.get();
                List<String> videoList = caseEntity.getVideoUrlList();
                if(index < 0 || index >= videoList.size()){
                    throw new DefaultException("index超出范围");
                }
                videoList.remove(index);
                caseEntity.setVideoUrlList(videoList);
                Case savedCase = caseRepository.save(caseEntity);
                return Optional.of(savedCase);
    }

    public String uploadFile(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        Optional<Case> existing = caseRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应case不存在，无法更新图片");

        var updating = existing.get();

        List<String> files = updating.getFiles();
        files.add(fileId);
        updating.setFiles(files);

        caseRepository.save(updating);

        return fileId;

    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        Optional<Case> existing = caseRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应case不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        caseRepository.save(updating);
        return fileId;

    }

}
