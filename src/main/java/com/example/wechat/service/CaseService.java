package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Case;
import com.example.wechat.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaseService {


    @Autowired
    private CaseRepository caseRepository;

    // 添加病例的业务逻辑
    public Optional<Case> addCase(Case newCase) {
        // 检查是否已经存在相同名称的病例
        Optional<Case> existingCase = caseRepository.findByName(newCase.getName());
        if (existingCase.isPresent()) {
            // 病例名称已存在，返回空Optional作为错误指示
            throw new DefaultException("本病例已存在");
        }

        // 病例名称不存在，添加新病例
        Case savedCase = caseRepository.save(newCase);
        return Optional.of(savedCase);
    }

    // 删除病例的业务逻辑
    public Optional<Case> deleteCase(Case caseToDelete) {
        // 检查病例是否存在
        Optional<Case> existingCase = caseRepository.findById(caseToDelete.getId());
        if (!existingCase.isPresent()) {
            // 病例不存在，返回空Optional作为错误指示
            throw new DefaultException("病例不存在");
        }

        // 病例存在，执行删除操作
        caseRepository.delete(caseToDelete);

        return existingCase;
    }
}
