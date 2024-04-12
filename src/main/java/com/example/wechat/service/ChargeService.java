package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Assay;
import com.example.wechat.model.Charge;
import com.example.wechat.repository.ChargeRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ChargeService {
    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private FileService fileService;
    /**
     * 添加一个新的科室信息。
     * @param charge 要添加的科室对象
     * @return 添加成功后的疾病对象
     * @throws NameAlreadyExistedException 如果科室名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws DefaultException            如果科室名不合法，则抛出 DefaultException 异常
     */
    public Charge addCharge(Charge charge) throws NameAlreadyExistedException, DefaultException {
        Optional<Charge> existedCharge = chargeRepository.findChargeByName(charge.getName());
        if (existedCharge.isPresent()) {
            throw new NameAlreadyExistedException("名字已存在");
        }
        NameChecker.nameIsLegal(charge.getName());
        Charge savedCharge = chargeRepository.save(charge);
        return savedCharge;
    }
    /**
     * 确保数据库中存在一个名称为 "待定" 的部门。
     * 如果该部门不存在，则尝试添加它；如果已经存在，则返回已存在的部门。
     * @return 存在或添加后的 "待定" 部门对象
     * @throws DefaultException 如果无法找到 "待定" 部门且尝试创建时出错，则抛出该异常
     */
    public Charge ensurePendingChargeExists() throws DefaultException {
        Charge pendingCharge = new Charge();
        pendingCharge.setName("待定");
        // 尝试添加"待定"类别，如果它已经存在，这个方法将不会抛出异常，而是返回已存在的类别
        try {
            return addCharge(pendingCharge);
        } catch (NameAlreadyExistedException ne) {
            // 如果因为类别名已存在而抛出异常，则尝试返回现有的"待定"类别
            return chargeRepository.findChargeByName("待定")
                    .orElseThrow(() -> new DefaultException("无法找到'待定'类别，且尝试创建时出错"));
        }
    }
    /**
     * 根据疾病名称删除科室信息。
     * @param id 要删除的科室id
     * @return 删除成功后返回被删除的疾病对象的 Optional，如果找不到对应id的科室则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应id的科室，则抛出 IdNotFoundException 异常
     */
    public Optional<Charge> deleteChargeById(ObjectId id) throws IdNotFoundException {
        Optional<Charge> existedCharge = chargeRepository.findById(id);
        if (!existedCharge.isPresent()) {
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应科室不存在，无法删除");
        }
        // 确保存在一个"待定"的Category
        Charge pendingCharge = ensurePendingChargeExists();
        // 更新引用了即将删除Category的Facility文档,暂时没写
        chargeRepository.delete(existedCharge.get());
        return existedCharge;
    }
    /**
     * 更新指定科室信息。
     *
     * @param charge 要更新的科室对象
     * @return 更新后的科室对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws NameAlreadyExistedException 如果修改了科室名称且新名称已存在，则抛出该异常
     * @throws IdNotFoundException         如果指定的科室 ID 不存在，则抛出该异常
     * @throws DefaultException            如果科室名不合法，则抛出 DefaultException 异常
     */
    public Optional<Charge> updateCharge(Charge charge) throws NameAlreadyExistedException, IdNotFoundException, DefaultException {
        Optional<Charge> chargeOriginal = chargeRepository.findChargeById(charge.getId());
        if (chargeOriginal.isPresent()) {
            Charge charge_editing = chargeOriginal.get();
            //如果修改了名字，那么对名字的唯一性进行校验
            if (!charge_editing.getName().equals(charge.getName())) {
                Optional<Charge> existedCharge = chargeRepository.findChargeByName(charge.getName());
                //存在一个和当前名字相同的病，则返回失败
                if (existedCharge.isPresent()) {
                    throw new NameAlreadyExistedException("科室名字已存在,更改失败");
                }
                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(charge.getName());
            }
            charge_editing.setName(charge.getName());
            charge_editing.setInfo(charge.getInfo());
            return Optional.of(chargeRepository.save(charge_editing));
        }
        throw new IdNotFoundException("id不存在,无法更新科室");
    }
    /**
     * 获取所有科室列表。
     * @return 所有科室的列表
     */
    public List<Charge> findAllCharges() {
        return chargeRepository.findAll();
    }

    public Optional<Charge> findChargeById(String id){
        Optional<Charge> existing = chargeRepository.findById(new ObjectId(id));
        if(!existing.isPresent()) throw new IdNotFoundException("对应收据不存在");
        else return existing;
    }

    public String uploadFile(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        Optional<Charge> existing = chargeRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应收据不存在，无法更新图片");

        var updating = existing.get();

        List<String> files = updating.getFiles();
        files.add(fileId);
        updating.setFiles(files);

        chargeRepository.save(updating);

        return fileId;

    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        Optional<Charge> existing = chargeRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应收据不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        chargeRepository.save(updating);
        return fileId;

    }
}
