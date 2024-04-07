package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Case;
import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.repository.CaseRepository;
import com.example.wechat.repository.CategoryRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CaseRepository caseRepository;


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

        //检查名字的合法性，如果不正确则抛出错误
        NameChecker.nameIsLegal(disease.getName());

        Disease savedDisease = diseaseRepository.save(disease);
        return savedDisease;
    }


    /**
     * 根据疾病名称删除疾病信息。
     *
     * @param id 要删除的疾病id
     * @return 删除成功后返回被删除的疾病对象的 Optional，如果找不到对应名称的疾病则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应名称的疾病，则抛出 IdNotFoundException 异常
     */
    public Optional<Disease> deleteDiseaseById(ObjectId id) throws IdNotFoundException{
        Optional<Disease> existedDisease = diseaseRepository.findById(id);

        //对应名字的disease不存在则报错
        if(!existedDisease.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应疾病不存在");
        }
        diseaseRepository.delete(existedDisease.get());

        //级联删除case
        List<Case> cases = caseRepository.findByDiseaseId(id);
        for(Case cas : cases){
            caseRepository.delete(cas);
        }
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

            //如果修改了名字，那么对名字的约束进行校验
            if(!dese.getName().equals(disease.getName())){
                Optional<Disease> existedDisease = diseaseRepository.findDiseaseByName(disease.getName());

                //存在一个和当前名字相同的病，则返回失败
                if(existedDisease.isPresent()){
                   throw new DefaultException("名字已存在");
                }

                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(disease.getName());
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
    public List<Disease> findDiseasesByCategoryId(ObjectId categoryId) throws IdNotFoundException {
        if(!categoryRepository.findById(categoryId).isPresent()) throw new IdNotFoundException("对应病种不存在");
        return diseaseRepository.findByCategoryId(categoryId);
    }



    /**
     * 根据疾病名称进行模糊匹配查询疾病列表。
     *
     * @param name 科室名称关键词
     * @return 符合模糊匹配条件的疾病列表
     */
    public List<Disease> findDiseasesByNameLike(String name) {
        // 构建一个正则表达式，进行不区分大小写的模糊匹配
        String regex = ".*" + name + ".*";
        return diseaseRepository.findDiseasesByNameLike(regex);
    }



    /**
     * 获取所有疾病列表。
     *
     * @return 所有疾病的列表
     */
    public List<Disease> findAllDiseases() {
        return diseaseRepository.findAll();
    }



    /**
     * 根据疾病ID查找疾病信息。
     *
     * @param id 疾病ID
     * @return 包含科室信息的 Optional 对象，如果找到则返回疾病信息，否则返回空 Optional
     * @throws IdNotFoundException 如果对应的疾病ID不存在，则抛出 IdNotFoundException 异常
     */
    public Optional<Disease> findDiseaseById(ObjectId id) throws IdNotFoundException{
        Optional<Disease> disease = diseaseRepository.findById(id);
        if(disease.isPresent()) return disease;
        else throw new IdNotFoundException("对应id不存在");
    }

}
