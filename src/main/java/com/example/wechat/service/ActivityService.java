package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.format.NameChecker;
import com.example.wechat.model.Activity;
import com.example.wechat.model.Assay;
import com.example.wechat.model.Procedure;
import com.example.wechat.repository.ActivityRepository;
import com.example.wechat.repository.AssayRepository;
import com.example.wechat.repository.ProcedureRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProcedureRepository procedureRepository;

    @Autowired
    private FileService fileService;

    /**
     * 添加一个新的角色活动信息。
     *
     * @param activity 要添加的活动对象
     * @return 添加成功后的活动对象
     * @throws NameAlreadyExistedException 如果活动名已经存在，则抛出 NameAlreadyExistedException 异常
     * @throws DefaultException            如果活动名不合法，则抛出 DefaultException 异常
     */
    public Activity addActivity(Activity activity) throws NameAlreadyExistedException, DefaultException {
        Optional<Activity> existedActivity = activityRepository.findActivityByName(activity.getName());
        if (existedActivity.isPresent()) {
            throw new NameAlreadyExistedException("名字已存在");
        }
        NameChecker.nameIsLegal(activity.getName());
        Activity saved = activityRepository.save(activity);
        return saved;
    }


    /**
     * 根据角色活动id删除角色活动信息。
     *
     * @param id 要删除的角色活动id
     * @return 删除成功后返回被删除的角色活动对象的 Optional
     * @throws IdNotFoundException 如果找不到对应id的角色活动，则抛出 IdNotFoundException 异常
     */
    public Optional<Activity> deleteActivityById(ObjectId id) throws IdNotFoundException {
        Optional<Activity> existed = activityRepository.findById(id);
        if (!existed.isPresent()) {
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应角色活动不存在，无法删除");
        }

        activityRepository.delete(existed.get());

        //对procedure级联删除
        List<Procedure>  procedures = procedureRepository.findByActivityId(id);
        for(Procedure p : procedures){
            procedureRepository.delete(p);
        }

        return existed;
    }



    /**
     * 更新指定角色活动信息。
     *
     * @param activity 要更新的角色活动对象
     * @return 更新后的角色活动对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws NameAlreadyExistedException 如果修改了橘色活动名称且新名称已存在，则抛出该异常
     * @throws IdNotFoundException         如果指定的角色活动 ID 不存在，则抛出该异常
     * @throws DefaultException            如果角色活动名不合法，则抛出 DefaultException 异常
     */
    public Optional<Activity> updateActivity(Activity activity) throws NameAlreadyExistedException, IdNotFoundException, DefaultException {
        Optional<Activity> activityOriginal = activityRepository.findById(activity.getId());
        if (activityOriginal.isPresent()) {
            Activity activity_editing = activityOriginal.get();
            //如果修改了名字，那么对名字的唯一性进行校验
            if (!activity_editing.getName().equals(activity.getName())) {
                Optional<Activity> existedActivity= activityRepository.findActivityByName(activity.getName());
                //存在一个和当前名字相同的病，则返回失败
                if (existedActivity.isPresent()) {
                    throw new NameAlreadyExistedException("角色活动名字已存在,更改失败");
                }
                //检查名字的合法性，如果不正确则抛出错误
                NameChecker.nameIsLegal(activity.getName());
            }

            return Optional.of(activityRepository.save(activity));
        }
        throw new IdNotFoundException("id不存在,无法更新角色活动");
    }


    /**
     * 获取所有活动列表。
     * @return 所有活动的列表
     */
    public List<Activity> findAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity>  findActivityById(String id) throws IdNotFoundException{
        Optional<Activity> optionalActivity = activityRepository.findById(new ObjectId(id));
        if(optionalActivity.isPresent()) return optionalActivity;
        else throw new IdNotFoundException("对应活动不存在");
    }
    public List<Activity> findActivityByRoleId(String id){
        return activityRepository.findByRoleId(new ObjectId(id));
    }

    public Optional<Activity> findActivityByName(String name){
        return activityRepository.findActivityByName(name);
    }

    public String uploadAvatar(MultipartFile file, String id)throws IOException {
        String fileId = fileService.uploadFile(file);
        ObjectId objectId = new ObjectId(id);
        var existing = activityRepository.findById(objectId);

        //存在性检查
        if (!existing.isPresent()) throw new IdNotFoundException("对应实体不存在，无法更新图片");

        var updating = existing.get();

        updating.setAvatar(fileId);
        activityRepository.save(updating);
        return fileId;

    }
    public void updateActivityPicUrl(ObjectId id, String picUrl) {
        var activityOptional = activityRepository.findById(id);
        if (activityOptional.isPresent()) {
            Activity activity = activityOptional.get();
            activity.getPicUrlList().add(picUrl); // 更新流程的图片URL
            activityRepository.save(activity); // 保存更改
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
    public void updateActivityVidUrl(ObjectId id, String vidUrl) {
        var activityOptional = activityRepository.findById(id);
        if (activityOptional.isPresent()) {
            Activity activity = activityOptional.get();
            activity.getVideoUrlList().add(vidUrl); // 更新流程的图片URL
            activityRepository.save(activity); // 保存更改
        } else {
            throw new IdNotFoundException("没有找到对应id"); // 抛出ID未找到异常
        }
    }
}
