package com.example.wechat.service;

import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.exception.NameNotFoundException;
import com.example.wechat.model.*;
import com.example.wechat.repository.ProcedureRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProcedureService {

    @Autowired
    private ProcedureRepository procedureRepository;

    @Autowired
    private  FileService fileService;

    /**
     * 添加一个新的流程信息。
     *
     * @param procedure 要添加的流程对象
     * @return 添加成功后的流程对象
     * @throws NameNotFoundException 如果对应该角色对应步骤已经存在，则抛出 NameNotFoundException 异常
     */
    public Procedure addProcedure(Procedure procedure) throws NameAlreadyExistedException {
        List<Procedure> existedProcedures = procedureRepository.findByRoleId(procedure.getRole().getId());

        //如果当前角色有相同名字的流程，那么创建失败
        for (Procedure existedProcedure : existedProcedures){
            if(existedProcedure.getIndex().equals(procedure.getIndex()))
                throw new NameAlreadyExistedException("当前角色已存在该流程");
        }

        //保存到数据库
        Procedure savedProcedure = procedureRepository.save(procedure);
        return savedProcedure;
    }



    /**
     * 根据流程id删除流程信息。
     *
     * @param id 要删除的流程id
     * @return 删除成功后返回被删除的流程对象的 Optional，如果找不到对应id的流程则返回空 Optional
     * @throws IdNotFoundException 如果找不到对应id的流程，则抛出 IdNotFoundException 异常
     */
    public Optional<Procedure> deleteProcedureById(ObjectId id) throws IdNotFoundException {
        Optional<Procedure> existedProcedure = procedureRepository.findById(id);
        Procedure procedure = existedProcedure.get();


        //对应名字的procedure不存在则报错
        if(!existedProcedure.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应流程不存在，无法删除");
        }

        //对删除进行持久化
        procedureRepository.delete(procedure);
        return existedProcedure;
    }

    /**
     * 更新指定流程信息。
     *
     * @param procedure 要更新的流程对象
     * @return 更新后的流程对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws IdNotFoundException        如果指定的流程 ID 不存在，则抛出该异常
     * @throws NameAlreadyExistedException  如果指定的流程名字已经存在，则抛出该异常
     */
    public Optional<Procedure> updateProcedure(Procedure procedure) throws IdNotFoundException,NameAlreadyExistedException{
        Optional<Procedure> procedureOriginal = procedureRepository.findById(procedure.getId());
        Integer name = procedureOriginal.get().getIndex();
        Role role = procedureOriginal.get().getRole();
        if(procedureOriginal.isPresent()){
           List<Procedure> procedures = procedureRepository.findByRoleId(procedure.getRole().getId());

           //如果给流程换了名字，那么对该角色列表中的所有流程名字进行唯一性检查
           if(!name.equals(procedure.getIndex())){
               for (Procedure p: procedures){
                   if (procedure.getIndex().equals(p.getIndex()) && !procedure.getId().equals(p.getId()))
                       throw new NameAlreadyExistedException("该流程已存在");
               }
           }

            //如果给流程换了角色，那么对该角色列表中的所有流程名字进行唯一性检查
            if(!role.equals(procedure.getRole())){
                for (Procedure p: procedures){
                    if (procedure.getIndex().equals(p.getIndex()))
                        throw new NameAlreadyExistedException("该流程已存在");
                }
            }




            return Optional.of(procedureRepository.save(procedure));
        }
        throw new IdNotFoundException("id不存在,无法更新步骤");
    }


    /**
     * 根据流程ID查找流程信息。
     *
     * @param id 流程ID
     * @return 包含流程信息的 Optional 对象，如果找到则返回流程信息，否则返回空 Optional
     * @throws IdNotFoundException 如果对应的流程ID不存在，则抛出 IdNotFoundException 异常
     */
    public Optional<Procedure> findProcedureById(ObjectId id) throws IdNotFoundException{
        Optional<Procedure> procedure = procedureRepository.findById(id);
        if(procedure.isPresent()) return procedure;
        else throw new IdNotFoundException("对应id不存在");
    }


    /**
     * 根据角色ID查找流程信息。
     *
     * @param id 角色ID
     * @return 包含流程的列表对象，如果找到则返回角色对应的流程信息，否则返回空 Optional
     */
    public List<Procedure> findProcedureByRoleId(ObjectId id) {
        List<Procedure> procedures = procedureRepository.findByRoleId(id);
        // 使用Stream API进行排序
        return procedures.stream()
                .sorted(Comparator.comparingInt(Procedure::getIndex))
                .collect(Collectors.toList());
    }


    /**
     * 上传流程的图片URL。
     *
     * @param id 流程ID
     * @param picUrl 图片URL
     * @throws IdNotFoundException 如果找不到对应的流程ID，则抛出IdNotFoundException
     */
    public void updateProcedurePicUrl(ObjectId id, String picUrl) {
        Optional<Procedure> procedureOptional = procedureRepository.findById(id);
        if (procedureOptional.isPresent()) {
            Procedure procedure = procedureOptional.get();
            procedure.getPicUrlList().add(picUrl); // 更新流程的图片URL
            procedureRepository.save(procedure); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }


    /**
     * 上传流程的视频URL。
     *
     * @param id 流程ID
     * @param vidUrl 视频URL
     * @throws IdNotFoundException 如果找不到对应的流程ID，则抛出IdNotFoundException
     */
    public void uploadProcedureVidUrl(ObjectId id, String vidUrl) {
        Optional<Procedure> procedureOptional = procedureRepository.findById(id);
        if (procedureOptional.isPresent()) {
            Procedure procedure = procedureOptional.get();
            procedure.getVideoUrlList().add(vidUrl); // 更新流程的图片URL
            procedureRepository.save(procedure); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }


    /**
     * 删除流程的图片URL。
     *
     * @param id 流程ID
     * @param picUrl 图片URL
     * @throws IdNotFoundException 如果找不到对应的流程ID，则抛出IdNotFoundException
     */
    public void deleteProcedurePicUrl(ObjectId id, String picUrl) {
        Optional<Procedure> procedureOptional = procedureRepository.findById(id);
        if (procedureOptional.isPresent()) {
            Procedure procedure = procedureOptional.get();

            procedure.getPicUrlList().remove(picUrl);// 更新流程的图片URL
            procedureRepository.save(procedure); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }

    /**
     * 删除流程的视频URL。
     *
     * @param id 流程ID
     * @param vidUrl 视频URL
     * @throws IdNotFoundException 如果找不到对应的流程ID，则抛出IdNotFoundException
     */
    public void deleteProcedureVidUrl(ObjectId id, String vidUrl) {
        Optional<Procedure> procedureOptional = procedureRepository.findById(id);
        if (procedureOptional.isPresent()) {
            Procedure procedure = procedureOptional.get();

            procedure.getVideoUrlList().remove(vidUrl); // 更新流程的图片URL
            procedureRepository.save(procedure); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }


    public String uploadFile(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = procedureRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应对象不存在，无法更新图片");

        var updating = existing.get();

        List<String> files = updating.getFiles();
        files.add(fileId);
        updating.setFiles(files);

        procedureRepository.save(updating);

        return fileId;

    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = procedureRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应实体不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        procedureRepository.save(updating);
        return fileId;

    }


}
