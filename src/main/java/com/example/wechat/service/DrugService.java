package com.example.wechat.service;

import com.example.wechat.model.Drug;
import com.example.wechat.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    public Drug addDrug(Drug drug) {
        // 添加药品的业务逻辑，例如，验证药品信息等
        return drugRepository.save(drug);
    }
}
