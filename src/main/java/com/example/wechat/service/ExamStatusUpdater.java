package com.example.wechat.service;

import com.example.wechat.model.Exam;
import com.example.wechat.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
public class ExamStatusUpdater {

    @Autowired
    private ExamRepository examRepository;

    @Scheduled(fixedRate = 60000)
    public void updateExamStatus() {
        List<Exam> exams = examRepository.findAll();
        Date now = new Date();
        exams.forEach(exam -> {
            if (!"被中止".equals(exam.getStatus())) {
                if (exam.getStartTime().after(now)) {
                    // 当前时间未到开始时间
                    exam.setStatus("未开始");
                } else if (exam.getStartTime().before(now) && exam.getEndTime().after(now)) {
                    // 当前时间在开始时间和结束时间之间
                    exam.setStatus("进行中");
                } else if (exam.getEndTime().before(now)) {
                    // 当前时间在结束时间之后
                    exam.setStatus("已过期");
                }
                examRepository.save(exam);
            }
        });
    }
}
