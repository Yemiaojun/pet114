package com.example.wechat.controller;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Disease;
import com.example.wechat.service.DiseaseService;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utils.Result;

import javax.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DiseaseControllerTests {

    @Mock
    private DiseaseService diseaseService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DiseaseController diseaseController;

    public DiseaseControllerTests() {
        MockitoAnnotations.initMocks(this);
    }

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

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddDiseaseFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Disease 对象和 DiseaseService 的行为
        Disease disease = new Disease(); // 创建一个疾病对象
        when(diseaseService.addDisease(any(Disease.class))).thenThrow(new DefaultException("")); // 模拟添加疾病失败

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.addDisease(disease, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddDiseaseUserNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.addDisease(new Disease(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }


    @Test
    public void testDeleteDiseaseSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟请求体包含疾病ID
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "5fc73dfac116601124123456");

        // 模拟 DiseaseService 的行为（成功删除疾病）
        when(diseaseService.deleteDiseaseById(any(ObjectId.class))).thenReturn(Optional.of(new Disease()));

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟请求体包含疾病ID
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "5fc73dfac116601124123456");

        // 模拟 DiseaseService 的行为（删除疾病失败）
        when(diseaseService.deleteDiseaseById(any(ObjectId.class))).thenThrow(new DefaultException(""));

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDiseaseUserNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.deleteDisease(new HashMap<>(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }


    @Test
    public void testUpdateDiseaseSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（成功更新疾病）
        Disease updatedDisease = new Disease();
        when(diseaseService.updateDisease(any(Disease.class))).thenReturn(Optional.of(updatedDisease));

        // 调用被测试的方法
        Disease diseaseToUpdate = new Disease();
        ResponseEntity<String> response = diseaseController.updateDisease(diseaseToUpdate, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDiseaseFailure() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DiseaseService 的行为（更新疾病失败）
        when(diseaseService.updateDisease(any(Disease.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        Disease diseaseToUpdate = new Disease();
        ResponseEntity<String> response = diseaseController.updateDisease(diseaseToUpdate, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDiseaseUserNotLoggedIn() {
        // 模拟会话中没有管理员权限信息（未登录）

        // 调用被测试的方法
        Disease diseaseToUpdate = new Disease();
        ResponseEntity<String> response = diseaseController.updateDisease(diseaseToUpdate, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备更新权限"), response.getBody());
    }

    @Test
    public void testFindAllDiseasesSuccess() {
        // 模拟会话中的用户登录
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DiseaseService 的行为（成功获取所有疾病信息）
        List<Disease> diseases = new ArrayList<>();
        diseases.add(new Disease());
        diseases.add(new Disease());
        when(diseaseService.findAllDiseases()).thenReturn(diseases);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findAllDiseases(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindAllDiseasesUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findAllDiseases(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }

    @Test
    public void testFindDiseasesByNameSuccess() {
        // 模拟会话中的用户登录
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DiseaseService 的行为（成功查找疾病信息）
        List<Disease> diseases = new ArrayList<>();
        diseases.add(new Disease());
        diseases.add(new Disease());
        when(diseaseService.findDiseasesByNameLike(anyString())).thenReturn(diseases);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findDiseasesByName("Cold", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindDiseasesByNameUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findDiseasesByName("Cold", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }

    @Test
    public void testFindDiseaseByIdSuccess() {
        // 模拟会话中的用户登录
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DiseaseService 的行为（成功查找疾病信息）
        Optional<Disease> disease = Optional.of(new Disease());
        when(diseaseService.findDiseaseById(any(ObjectId.class))).thenReturn(disease);

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findDiseaseById("5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindDiseaseByIdUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = diseaseController.findDiseaseById("validObjectId", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }
}
