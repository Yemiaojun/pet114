package com.example.wechat.service;

import com.example.wechat.model.Drug;
import com.example.wechat.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    public Drug addDrug(Drug drug) {
        // 添加药品的业务逻辑，例如，验证药品信息等
        return drugRepository.save(drug);
    }

    public Optional<Drug> updateDrug(Drug drug) {
        if (drug.getId() == null) {
            // 如果药品ID不存在，表示这不是一个有效的更新操作
            return Optional.empty();
        }

        // 检查药品是否存在
        boolean exists = drugRepository.existsById(drug.getId());
        if (!exists) {
            // 药品不存在，返回空Optional
            return Optional.empty();
        }

        // 药品存在，执行更新操作
        Drug updatedDrug = drugRepository.save(drug);
        return Optional.of(updatedDrug);
    }
}
