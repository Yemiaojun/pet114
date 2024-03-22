package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Disease;
import com.example.wechat.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

/**
 * DiseaseService 提供了对 Disease 实体的操作服务。
 */
@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;


    /**
     * 添加一个新的疾病信息。
     *
     * @param disease 要添加的疾病对象
     * @return 添加成功后的疾病对象
     * @throws DefaultException 如果疾病名已经存在，则抛出 DefaultException 异常
     */
    public Disease addDisease(Disease disease) throws DefaultException{
        Optional<Disease> existedDisease = diseaseRepository.findDiseaseByName(disease.getName());

        //对应名字的disease存在则报错
        if(existedDisease.isPresent()){
            throw new DefaultException("名字已存在");
        }
        Disease savedDisease = diseaseRepository.save(disease);
        return savedDisease;
    }


    /**
     * 根据疾病名称删除疾病信息。
     *
     * @param name 要删除的疾病名称
     * @return 删除成功后返回被删除的疾病对象的 Optional，如果找不到对应名称的疾病则返回空 Optional
     * @throws DefaultException 如果找不到对应名称的疾病，则抛出 DefaultException 异常
     */
    public Optional<Disease> deleteDiseaseByName(String name) throws DefaultException{
        Optional<Disease> existedDisease = diseaseRepository.findDiseaseByName(name);

        //对应名字的disease不存在则报错
        if(!existedDisease.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new DefaultException("名字不存在");
        }
        diseaseRepository.delete(existedDisease.get());
        return existedDisease;
    }


    /**
     * 更新疾病信息。
     *
     * @param disease 要更新的疾病对象
     * @return 更新成功后返回更新后的疾病对象的 Optional，如果找不到对应 ID 的疾病则返回空 Optional
     * @throws DefaultException 如果疾病名已经存在，则抛出 DefaultException 异常
     */
    public Optional<Disease> updateDisease(Disease disease) throws DefaultException{
        Optional<Disease> diseaseOriginal = diseaseRepository.findDiseaseById(disease.getId());
        if(diseaseOriginal.isPresent()){
            Disease dese = diseaseOriginal.get();

            //如果修改了名字，那么对名字的唯一性进行校验
            if(!dese.getName().equals(disease.getName())){
                Optional<Disease> existedDisease = diseaseRepository.findDiseaseByName(disease.getName());

                //存在一个和当前名字相同的病，则返回失败
                if(existedDisease.isPresent()){
                   throw new DefaultException("名字已存在");
                }
            }
            dese.setName(disease.getName());
            dese.setCategory(disease.getCategory());
            dese.setText(disease.getText());
            return Optional.of(diseaseRepository.save(dese));
        }
        throw new DefaultException("id不存在");
    }

    /**
     * 根据分类 ID 获取该分类下的所有疾病信息。
     *
     * @param categoryId 分类 ID
     * @return 返回该分类下的所有疾病信息列表
     */
    public List<Disease> getDiseasesByCategoryId(ObjectId categoryId) {
        return diseaseRepository.findByCategoryId(categoryId);
    }


}
