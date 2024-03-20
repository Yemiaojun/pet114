package com.example.wechat.controller;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Disease;
import com.example.wechat.service.DiseaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DiseaseControllerTests {

    @Mock
    private DiseaseService diseaseService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DiseaseController diseaseController;

    @Test
    public void testAddDiseaseSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Disease 对象和 DiseaseService 的行为
        Disease disease = new Disease(); // 创建一个疾病对象
        when(diseaseService.addDisease(any(Disease.class))).thenReturn(disease); // 模拟添加疾病成功

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.addDisease(disease, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testAddDiseaseFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 模拟 Disease 对象和 DiseaseService 的行为
        Disease disease = new Disease(); // 创建一个疾病对象
        when(diseaseService.addDisease(any(Disease.class))).thenThrow(new DefaultException("名字已存在")); // 模拟添加疾病失败

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.addDisease(disease, session);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseSuccess() {
        // 模拟会话中的用户ID和权限（管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（成功删除疾病）
        String diseaseName = "Test Disease";
        when(diseaseService.deleteDiseaseByName(diseaseName)).thenReturn(Optional.of(new Disease()));

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease(diseaseName, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseNotFound() {
        // 模拟会话中的用户ID和权限（管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（未找到要删除的疾病）
        String diseaseName = "Nonexistent Disease";
        when(diseaseService.deleteDiseaseByName(diseaseName)).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease(diseaseName, session);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseUnauthorized() {
        // 模拟会话中的用户ID和权限（非管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease("Any Disease", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease("Any Disease", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void testUpdateDiseaseSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（成功更新疾病）
        Disease updatedDisease = new Disease(); // 模拟更新后的疾病对象
        when(diseaseService.updateDisease(any(Disease.class))).thenReturn(Optional.of(updatedDisease));

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.updateDisease(new Disease(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDiseaseNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（未找到要更新的疾病）
        when(diseaseService.updateDisease(any(Disease.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.updateDisease(new Disease(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDiseaseUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.updateDisease(new Disease(), session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDiseaseNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.updateDisease(new Disease(), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

