package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.exception.NameNotFoundException;
import com.example.wechat.model.Category;
import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Facility;
import com.example.wechat.repository.DepartmentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;


    /**
     * 添加一个新的科室信息。
     *
     * @param department 要添加的科室对象
     * @return 添加成功后的疾病对象
     * @throws NameAlreadyExistedException 如果科室名已经存在，则抛出 NameAlreadyExistedException 异常
     */
    public Department addDepartment(Department department) throws NameAlreadyExistedException {
        Optional<Department> existedDepatment = departmentRepository.findDepartmentByName(department.getName());

        //对应名字的disease存在则报错
        if(existedDepatment.isPresent()){
            throw new NameAlreadyExistedException("名字已存在");
        }
        Department savedDepartment = departmentRepository.save(department);
        return savedDepartment;
    }

    /**
     * 确保数据库中存在一个名称为 "待定" 的部门。
     * 如果该部门不存在，则尝试添加它；如果已经存在，则返回已存在的部门。
     *
     * @return 存在或添加后的 "待定" 部门对象
     * @throws DefaultException 如果无法找到 "待定" 部门且尝试创建时出错，则抛出该异常
     */
    public Department ensurePendingDepartmentyExists() throws DefaultException{
        Department pendingDepartment = new Department();
        pendingDepartment.setName("待定");

        // 尝试添加"待定"类别，如果它已经存在，这个方法将不会抛出异常，而是返回已存在的类别
        try {
            return addDepartment(pendingDepartment);
        } catch (NameAlreadyExistedException ne) {
            // 如果因为类别名已存在而抛出异常，则尝试返回现有的"待定"类别
            return departmentRepository.findDepartmentByName("待定")
                    .orElseThrow(() -> new DefaultException("无法找到'待定'类别，且尝试创建时出错"));
        }
    }

    /**
     * 根据疾病名称删除科室信息。
     *
     * @param name 要删除的科室名称
     * @return 删除成功后返回被删除的疾病对象的 Optional，如果找不到对应名称的科室则返回空 Optional
     * @throws NameNotFoundException 如果找不到对应名称的科室，则抛出 NameNotFoundException 异常
     */
    public Optional<Department> deleteDepartmentByName(String name) throws NameNotFoundException {
        Optional<Department> existedDepartment = departmentRepository.findDepartmentByName(name);

        //对应名字的department不存在则报错
        if(!existedDepartment.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new NameNotFoundException("对应科室不存在，无法删除");
        }

        // 确保存在一个"待定"的Category
        Department pendingDepartment = ensurePendingDepartmentyExists();

        // 更新引用了即将删除Category的Facility文档,暂时没写




        departmentRepository.delete(existedDepartment.get());
        return existedDepartment;
    }

    /**
     * 更新指定科室信息。
     *
     * @param department 要更新的科室对象
     * @return 更新后的科室对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws NameAlreadyExistedException 如果修改了科室名称且新名称已存在，则抛出该异常
     * @throws IdNotFoundException        如果指定的科室 ID 不存在，则抛出该异常
     */
    public Optional<Department> updateDepartment(Department department) throws NameAlreadyExistedException, IdNotFoundException {
        Optional<Department> departmentOriginal = departmentRepository.findDepartmentById(department.getId());
        if(departmentOriginal.isPresent()){
            Department department_editing = departmentOriginal.get();

            //如果修改了名字，那么对名字的唯一性进行校验
            if(!department_editing.getName().equals(department.getName())){
                Optional<Department> existedDepartment = departmentRepository.findDepartmentByName(department.getName());

                //存在一个和当前名字相同的病，则返回失败
                if(existedDepartment.isPresent()){
                    throw new NameAlreadyExistedException("科室名字已存在,更改失败");
                }
            }
            department_editing.setName(department.getName());
            department_editing.setInfo(department.getInfo());
            department_editing.setRoleList(department.getRoleList());

            return Optional.of(departmentRepository.save(department_editing));
        }
        throw new IdNotFoundException("id不存在,无法更新科室");
    }



}