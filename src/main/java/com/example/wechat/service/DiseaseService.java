package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Disease;
import com.example.wechat.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.bson.types.ObjectId;
@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    public Disease addDisease(Disease disease) throws DefaultException{
        Optional<Disease> existedDisease = diseaseRepository.findDiseaseByName(disease.getName());

        //对应名字的disease存在则报错
        if(existedDisease.isPresent()){
            throw new DefaultException("名字已存在");
        }
        Disease savedDisease = diseaseRepository.save(disease);
        return savedDisease;
    }

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


}
