package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.exception.NameNotFoundException;
import com.example.wechat.model.Department;
import com.example.wechat.model.Facility;
import com.example.wechat.model.Procedure;
import com.example.wechat.repository.ProcedureRepository;
import org.apache.tomcat.jni.Proc;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedureService {

    @Autowired
    private ProcedureRepository procedureRepository;

    /**
     * 添加一个新的流程信息。
     *
     * @param procedure 要添加的流程对象
     * @return 添加成功后的流程对象
     * @throws NameNotFoundException 如果对应科室对应步骤已经存在，则抛出 NameNotFoundException 异常
     */
    public Procedure addProcedure(Procedure procedure) throws NameAlreadyExistedException {
        List<Procedure> existedProcedures = procedureRepository.findByFacilityId(procedure.getFacility().getId());

        //如果当前procedure插入位置在列表中间，则对其后步骤的step依次加一
        if(procedure.getStep() <= existedProcedures.size()){
            for(Procedure p : existedProcedures){
                if(p.getStep() >= procedure.getStep()) {
                    p.setStep(p.getStep() + 1);
                    procedureRepository.save(p);
                }
            }
        }


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
        List<Procedure> procedures = procedureRepository.findByFacilityId(procedure.getFacility().getId());

        //对应名字的procedure不存在则报错
        if(!existedProcedure.isPresent()){
            // id不存在，返回空Optional作为错误指示
            throw new IdNotFoundException("对应步骤不存在，无法删除");
        }

        //删除后，如果后续还有步骤，向step递减
        for(Procedure p: procedures){
            if(p.getStep() > procedure.getStep()) {
                p.setStep(p.getStep() - 1);
                procedureRepository.save(p);
            }

        }


        procedureRepository.delete(procedure);
        return existedProcedure;
    }

    /**
     * 更新指定流程信息。
     *
     * @param procedure 要更新的流程对象
     * @return 更新后的流程对象的 Optional 包装，如果更新成功则包含更新后的对象，否则为空
     * @throws IdNotFoundException        如果指定的流程 ID 不存在，则抛出该异常
     */
    public Optional<Procedure> updateProcedure(Procedure procedure) throws IdNotFoundException {
        Optional<Procedure> procedureOriginal = procedureRepository.findById(procedure.getId());
        if(procedureOriginal.isPresent()){
           Procedure procedure_editing = procedureOriginal.get();
           List<Procedure> procedures = procedureRepository.findByFacilityId(procedure_editing.getFacility().getId());

            //如果修改了step则对后续步骤step的一致性进行修改
            if(!procedure_editing.getStep().equals(procedure.getStep())){
                int step_editted = procedure.getStep();
                int step_editting = procedure_editing.getStep();

                //如果步骤提后，那么则对之间的步骤进行-1操作
                if(step_editting < step_editted){
                    for (Procedure p: procedures) {
                        int s = p.getStep();
                        if(s<step_editted && s>step_editting){
                            p.setStep(p.getStep()-1);
                            procedureRepository.save(p);
                        }
                    }
                }
                //如果步骤提前，那么对区间的步骤进行+1操作
                else{
                    for (Procedure p: procedures) {
                        int s = p.getStep();
                        if(s>=step_editted && s<step_editting){
                            p.setStep(p.getStep()+1);
                            procedureRepository.save(p);
                        }
                    }
                }
            }


            return Optional.of(procedureRepository.save(procedure));
        }
        throw new IdNotFoundException("id不存在,无法更新步骤");
    }




    /**
     * 根据设施ID查找科室信息。
     *
     * @param id 科室ID
     * @return 包含科室信息的 Optional 对象，如果找到则返回科室对应的流程信息，否则返回空 Optional
     */
    public List<Procedure> findProcedureByFacilityId(ObjectId id) {

        List<Procedure> procedures = procedureRepository.findByFacilityId(id);
        return procedures;
    }

}
