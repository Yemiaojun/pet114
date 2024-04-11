package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Vaccinum;
import com.example.wechat.repository.VaccinumRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VaccinumService {
    @Autowired
    private VaccinumRepository vaccinumRepository;
    /**
     * 添加一个新的科室信息。
     * @param vaccinum 要添加的科室对象
     * @return 添加成功后的疾病对象
     * @throws NameAlreadyExistedException 如果科室名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws DefaultException            如果科室名不合法，则抛出 DefaultException 异常
     */
    public Vaccinum addVaccinum(Vaccinum vaccinum) throws NameAlreadyExistedException, DefaultException {
        Optional<Vaccinum> existedVaccinum = vaccinumRepository.findVaccinumByName(vaccinum.getName());
        if (existedVaccinum.isPresent()) {
            throw new NameAlreadyExistedException("名字已存在");
        }
        NameChecker.nameIsLegal(vaccinum.getName());
        Vaccinum savedVaccinum = vaccinumRepository.save(vaccinum);
        return savedVaccinum;
    }
    /**
     * 确保数据库中存在一个名称为 "待定" 的部门。
     * 如果该部门不存在，则尝试添加它；如果已经存在，则返回已存在的部门。
     * @return 存在或添加后的 "待定" 部门对象
     * @throws DefaultException 如果无法找到 "待定" 部门且尝试创建时出错，则抛出该异常
     */
    public Vaccinum ensurePendingVaccinumExists() throws DefaultException {
        Vaccinum pendingVaccinum = new Vaccinum();
        pendingVaccinum.setName("待定");
        // 尝试添加"待定"类别，如果它已经存在，这个方法将不会抛出异常，而是返回已存在的类别
        try {
            return addVaccinum(pendingVaccinum);
        } catch (NameAlreadyExistedException ne) {
            // 如果因为类别名已存在而抛出异常，则尝试返回现有的"待定"类别
            return vaccinumRepository.findVaccinumByName("待定")
                    .orElseThrow(() -> new DefaultException("无法找到'待定'类别，且尝试创建时出错"));
        }
    }
    /**
     * 根据疾病名称删除科室信息。
     * @param id 要删除的科室id
     * @return 删除成功后返回被删除的疾病对象的 Optional，如果找不到对应id的科室则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应id的科室，则抛出 IdNotFoundException 异常
     */
    public Optional<Vaccinum> deleteVaccinumById(ObjectId id) throws IdNotFoundException {
        Optional<Vaccinum> existedVaccinum = vaccinumRepository.findById(id);
        if (!existedVaccinum.isPresent()) {
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应科室不存在，无法删除");
        }
        // 确保存在一个"待定"的Category
        Vaccinum pendingVaccinum = ensurePendingVaccinumExists();
        // 更新引用了即将删除Category的Facility文档,暂时没写
        vaccinumRepository.delete(existedVaccinum.get());
        return existedVaccinum;
    }
    /**
     * 更新指定科室信息。
     *
     * @param vaccinum 要更新的科室对象
     * @return 更新后的科室对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws NameAlreadyExistedException 如果修改了科室名称且新名称已存在，则抛出该异常
     * @throws IdNotFoundException         如果指定的科室 ID 不存在，则抛出该异常
     * @throws DefaultException            如果科室名不合法，则抛出 DefaultException 异常
     */
    public Optional<Vaccinum> updateVaccinum(Vaccinum vaccinum) throws NameAlreadyExistedException, IdNotFoundException, DefaultException {
        Optional<Vaccinum> vaccinumOriginal = vaccinumRepository.findVaccinumById(vaccinum.getId());
        if (vaccinumOriginal.isPresent()) {
            Vaccinum vaccinum_editing = vaccinumOriginal.get();
            //如果修改了名字，那么对名字的唯一性进行校验
            if (!vaccinum_editing.getName().equals(vaccinum.getName())) {
                Optional<Vaccinum> existedVaccinum = vaccinumRepository.findVaccinumByName(vaccinum.getName());
                //存在一个和当前名字相同的病，则返回失败
                if (existedVaccinum.isPresent()) {
                    throw new NameAlreadyExistedException("科室名字已存在,更改失败");
                }
                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(vaccinum.getName());
            }
            vaccinum_editing.setName(vaccinum.getName());
            vaccinum_editing.setInfo(vaccinum.getInfo());
            return Optional.of(vaccinumRepository.save(vaccinum_editing));
        }
        throw new IdNotFoundException("id不存在,无法更新科室");
    }
    /**
     * 获取所有科室列表。
     * @return 所有科室的列表
     */
    public List<Vaccinum> findAllVaccinums() {
        return vaccinumRepository.findAll();
    }
}
