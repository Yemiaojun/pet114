package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Case;
import com.example.wechat.model.Drug;
import com.example.wechat.model.Facility;
import com.example.wechat.repository.DrugRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private FileService fileService;

    /**
     * 添加一个新的药品信息。
     *
     * @param drug 要添加的药品对象
     * @return 添加成功后的药品对象
     * @throws NameAlreadyExistedException 如果药品名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws DefaultException 如果药品名字不合法，则抛出DefaultException异常
     */
    public Drug addDrug(Drug drug) throws NameAlreadyExistedException, DefaultException {
        Optional<Drug> existedDrug = drugRepository.findDrugByName(drug.getName());

        //对应名字的drug存在则报错
        if(existedDrug.isPresent()){
            throw new NameAlreadyExistedException("名字已存在");
        }

        //检查名字的合法性，如果不正确则抛出错误
        NameChecker.nameIsLegal(drug.getName());

        Drug savedDrug = drugRepository.save(drug);
        return savedDrug;
    }





    /**
     * 根据药品名称删除药品信息。
     *
     * @param id 要删除的药品id
     * @return 删除成功后返回被删除的药品对象的 Optional，如果找不到对应名称的药品则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应名称的药品，则抛出 IdNotFoundException 异常
     */
    public Optional<Drug> deleteDrugById(ObjectId id) throws IdNotFoundException {
        Optional<Drug> existedDrug = drugRepository.findById(id);

        //对应名字的drug不存在则报错
        if(!existedDrug.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应药品不存在");
        }
        drugRepository.delete(existedDrug.get());
        return existedDrug;
    }



    /**
     * 更新药品信息。
     *
     * @param drug 要更新的药品对象
     * @return 更新成功后返回更新后的药品对象的 Optional，如果找不到对应 ID 的药品则返回空 Optional
     * @throws NameAlreadyExistedException 如果药品名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws IdNotFoundException 如果药品id不存在，则抛出IdNotFoundException
     * @throws DefaultException 如果药品名字不合法，则抛出DefaultException异常
     */
    public Optional<Drug> updateDrug(Drug drug) throws NameAlreadyExistedException, IdNotFoundException,DefaultException {
        Optional<Drug> drugOriginal = drugRepository.findById(drug.getId());
        if(drugOriginal.isPresent()){
            Drug drugEntity = drugOriginal.get();

            //如果修改了名字，那么对名字的唯一性进行校验
            if(!drugEntity.getName().equals(drug.getName())){
                Optional<Drug> existedDrug = drugRepository.findDrugByName(drug.getName());

                //存在一个和当前名字相同的设备，则返回失败
                if(existedDrug.isPresent()){
                    throw new NameAlreadyExistedException("当前药品已存在");
                }

                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(drug.getName());
            }

            return Optional.of(drugRepository.save(drug));
        }
        throw new IdNotFoundException("id不存在");
    }


    /**
     * 根据药品名称进行模糊匹配查询药品列表。
     *
     * @param name 药品名称关键词
     * @return 符合模糊匹配条件的药品列表
     */
    public List<Drug> findDrugsByNameLike(String name) {
        // 构建一个正则表达式，进行不区分大小写的模糊匹配
        String regex = ".*" + name + ".*";
        return drugRepository.findDrugByNameLike(regex);
    }


    /**
     * 获取所有药品列表。
     *
     * @return 所有药品的列表
     */
    public List<Drug> findAllDrugs() {
        return drugRepository.findAll();
    }


    /**
     * 根据药品ID查找设施信息。
     *
     * @param id 药品ID
     * @return 包含药品信息的 Optional 对象，如果找到则返回药品信息，否则返回空 Optional
     * @throws IdNotFoundException 如果对应的药品ID不存在，则抛出 IdNotFoundException 异常
     */
    public Optional<Drug> findDrugById(ObjectId id) throws IdNotFoundException{
        Optional<Drug> drug = drugRepository.findById(id);
        if(drug.isPresent()) return drug;
        else throw new IdNotFoundException("对应id不存在");
    }



    /**
     * 更新药品的图片URL。
     *
     * @param id 药品ID
     * @param picUrl 图片URL
     * @throws IdNotFoundException 如果找不到对应的药品ID，则抛出IdNotFoundException
     */
    public void updateDrugPicUrl(ObjectId id, String picUrl) {
        Optional<Drug> drugOptional = drugRepository.findById(id);
        if (drugOptional.isPresent()) {
            Drug drug = drugOptional.get();
            drug.setPicUrl(picUrl); // 更新药品的图片URL
            drugRepository.save(drug); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }


    public String uploadFile(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = drugRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应对象不存在，无法更新图片");

        var updating = existing.get();

        List<String> files = updating.getFiles();
        files.add(fileId);
        updating.setFiles(files);

        drugRepository.save(updating);

        return fileId;

    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = drugRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应实体不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        drugRepository.save(updating);
        return fileId;

    }



}
