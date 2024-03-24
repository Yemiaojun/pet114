package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.exception.NameNotFoundException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Facility;
import com.example.wechat.model.User;
import com.example.wechat.repository.FacilityRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;


    /**
     * 添加一个新的设备信息。
     *
     * @param facility 要添加的设备对象
     * @return 添加成功后的设备对象
     * @throws NameAlreadyExistedException 如果设备名已经存在，则抛出 NameAlreadyExistedException 异常
     */
    public Facility addFacility(Facility facility) throws NameAlreadyExistedException {
        Optional<Facility> existedFacility = facilityRepository.findFacilityByName(facility.getName());

        //对应名字的facility存在则报错
        if(existedFacility.isPresent()){
            throw new NameAlreadyExistedException("名字已存在");
        }
        Facility savedFacility = facilityRepository.save(facility);
        return savedFacility;
    }

    /**
     * 根据科室名称删除设备信息。
     *
     * @param id 要删除的设备id
     * @return 删除成功后返回被删除的设备对象的 Optional，如果找不到对应名称的设备则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应名称的设备，则抛出 IdNotFoundException 异常
     */
    public Optional<Facility> deleteFacilityById(ObjectId id) throws IdNotFoundException {
        Optional<Facility> existedFacility = facilityRepository.findById(id);

        //对应名字的facility不存在则报错
        if(!existedFacility.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应设备不存在");
        }
        facilityRepository.delete(existedFacility.get());
        return existedFacility;
    }



    /**
     * 更新设备信息。
     *
     * @param facility 要更新的设备对象
     * @return 更新成功后返回更新后的设备对象的 Optional，如果找不到对应 ID 的设备则返回空 Optional
     * @throws NameAlreadyExistedException 如果设备名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws IdNotFoundException 如果设备id不存在，则抛出IdNotFoundException
     */
    public Optional<Facility> updateFacility(Facility facility) throws NameAlreadyExistedException, IdNotFoundException {
        Optional<Facility> facilityOriginal = facilityRepository.findById(facility.getId());
        if(facilityOriginal.isPresent()){
            Facility facilityEntity = facilityOriginal.get();

            //如果修改了名字，那么对名字的唯一性进行校验
            if(!facilityEntity.getName().equals(facility.getName())){
                Optional<Facility> existedFacility = facilityRepository.findFacilityByName(facility.getName());

                //存在一个和当前名字相同的设备，则返回失败
                if(existedFacility.isPresent()){
                    throw new NameAlreadyExistedException("当前设备已存在");
                }
            }

            return Optional.of(facilityRepository.save(facility));
        }
        throw new IdNotFoundException("id不存在");
    }




    /**
     * 根据设施名称进行模糊匹配查询设施列表。
     *
     * @param name 设施名称关键词
     * @return 符合模糊匹配条件的设施列表
     */
    public List<Facility> findFacilitiesByNameLike(String name) {
        // 构建一个正则表达式，进行不区分大小写的模糊匹配
        String regex = ".*" + name + ".*";
        return facilityRepository.findFacilityByNameLike(regex);
    }


    /**
     * 获取所有设施列表。
     *
     * @return 所有设施的列表
     */
    public List<Facility> findAllFacilities() {
        return facilityRepository.findAll();
    }


    /**
     * 根据设施ID查找设施信息。
     *
     * @param id 设施ID
     * @return 包含设施信息的 Optional 对象，如果找到则返回设施信息，否则返回空 Optional
     * @throws IdNotFoundException 如果对应的设施ID不存在，则抛出 IdNotFoundException 异常
     */
    public Optional<Facility> findFacilityById(ObjectId id) throws IdNotFoundException{
        Optional<Facility> facility = facilityRepository.findById(id);
        if(facility.isPresent()) return facility;
        else throw new IdNotFoundException("对应id不存在");
    }



}
