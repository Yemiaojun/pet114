package com.example.wechat.service;


import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Category;
import com.example.wechat.model.Department;
import com.example.wechat.model.Facility;
import com.example.wechat.model.Role;
import com.example.wechat.repository.DepartmentRepository;
import com.example.wechat.repository.RoleRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FileService fileService;




    /**
     * 添加一个新的角色信息。
     *
     * @param role 要添加的角色对象
     * @return 添加成功后的角色对象
     * @throws NameAlreadyExistedException 如果角色名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws DefaultException 如果角色名字不合法，则抛出DefaultException 异常
     */
    public Role addRole(Role role) throws NameAlreadyExistedException {
        Optional<Role> existedRole = roleRepository.findRoleByName(role.getName());

        //对应名字的role存在则报错
        if(existedRole.isPresent()){
            throw new NameAlreadyExistedException("角色名字已存在");
        }

        //检查名字的合法性，如果不正确则抛出错误
        NameChecker.nameIsLegal(role.getName());

        Role savedRole = roleRepository.save(role);
        return savedRole;
    }

    /**
     * 根据角色名称删除设备信息。
     *
     * @param id 要删除的角色id
     * @return 删除成功后返回被删除的角色对象的 Optional，如果找不到对应名称的角色则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应名称的角色，则抛出 IdNotFoundException 异常
     */
    public Optional<Role> deleteRoleById(ObjectId id) throws IdNotFoundException {
        Optional<Role> existedRole = roleRepository.findById(id);

        //对应名字的role不存在则报错
        if(!existedRole.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应角色不存在");
        }
        roleRepository.delete(existedRole.get());

        // 不要去做Role了。
        //移除department中的对应roleId
        //List<Department> departments = departmentService.findAllDepartments();
        //for(Department department : departments){
        //    List<Role> roles = department.getRoleList();
        //    for(Role role : roles){
        //        if(role.getId().equals(id)){
        //            roles.remove(role);
        //            department.setRoleList(roles);
        //            departmentService.updateDepartment(department);
        //            break;
        //        }
        //    }
        // }
        return existedRole;
    }


    /**
     * 更新角色信息。
     *
     * @param role 要更新的角色对象
     * @return 更新成功后返回更新后的角色对象的 Optional，如果找不到对应 ID 的角色则返回空 Optional
     * @throws NameAlreadyExistedException 如果角色名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws IdNotFoundException 如果角色id不存在，则抛出IdNotFoundException
     * @throws DefaultException 如果角色名字不合法，则抛出DefaultException
     */
    public Optional<Role> updateRole(Role role) throws NameAlreadyExistedException, IdNotFoundException, DefaultException {
        Optional<Role> roleOriginal = roleRepository.findById(role.getId());
        if(roleOriginal.isPresent()){
            Role roleEntity = roleOriginal.get();

            //如果修改了名字，那么对名字的唯一性进行校验
            if(!roleEntity.getName().equals(role.getName())){
                Optional<Role> existedRole = roleRepository.findRoleByName(role.getName());

                //存在一个和当前名字相同的角色，则返回失败
                if(existedRole.isPresent()){
                    throw new NameAlreadyExistedException("当前角色已存在");
                }

                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(role.getName());
            }

            return Optional.of(roleRepository.save(role));
        }
        throw new IdNotFoundException("id不存在");
    }


    /**
     * 获取所有角色列表。
     *
     * @return 所有角色的列表
     */
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }



    /**
     * 根据角色ID查找角色信息。
     *
     * @param id 角色ID
     * @return 包含角色信息的 Optional 对象，如果找到则返回角色信息，否则返回空 Optional
     * @throws IdNotFoundException 如果对应的角色ID不存在，则抛出 IdNotFoundException 异常
     */
    public Optional<Role> findRoleById(ObjectId id) throws IdNotFoundException{
        Optional<Role> role = roleRepository.findById(id);
        if(role.isPresent()) return role;
        else throw new IdNotFoundException("对应id不存在");
    }


    public Role ensurePendingRoleExists() {
        Role pendingRole = new Role();
        pendingRole.setName("待定");

        // 尝试添加"待定"类别，如果它已经存在，这个方法将不会抛出异常，而是返回已存在的类别
        try {
            return addRole(pendingRole);
        } catch (DefaultException e) {
            // 如果因为类别名已存在而抛出异常，则尝试返回现有的"待定"类别
            return  roleRepository.findRoleByName("待定")
                    .orElseThrow(() -> new DefaultException("无法找到'待定'类别，且尝试创建时出错"));
        }
    }

    public String uploadFile(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = roleRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应对象不存在，无法更新图片");

        var updating = existing.get();

        List<String> files = updating.getFiles();
        files.add(fileId);
        updating.setFiles(files);

        roleRepository.save(updating);

        return fileId;

    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = roleRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应实体不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        roleRepository.save(updating);
        return fileId;

    }


    public Optional<Role> findRoleByName(String name){
       return roleRepository.findRoleByName(name);

    }


}
