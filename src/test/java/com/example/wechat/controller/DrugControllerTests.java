package com.example.wechat.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.exception.NameNotFoundException;
import com.example.wechat.model.Drug;
import com.example.wechat.service.DrugService;
import com.example.wechat.service.FileStorageService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DrugControllerTests {

    @Mock
    private DrugService drugService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DrugController drugController;

    @Mock
    private FileStorageService fileStorageService;

    @Test
    public void testAddDrugSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Drug 对象和 DrugService 的行为
        Drug drug = new Drug(); // 创建一个药品对象
        when(drugService.addDrug(any(Drug.class))).thenReturn(drug); // 模拟添加药品成功

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.addDrug(drug, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddDrugFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Drug 对象和 DrugService 的行为
        Drug drug = new Drug(); // 创建一个药品对象
        when(drugService.addDrug(any(Drug.class))).thenThrow(new NameAlreadyExistedException("")); // 模拟添加药品失败

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.addDrug(drug, session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddDrugUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("userId")).thenReturn("1"); // 模拟用户ID
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.addDrug(new Drug(), session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddDrugNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.addDrug(new Drug(), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDrugSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 drugService 的行为（成功删除药品）
        ObjectId drugId = new ObjectId(); // 模拟药品ID
        Drug deletedDrug = new Drug(); // 模拟已删除的药品对象
        when(drugService.deleteDrugById(drugId)).thenReturn(Optional.of(deletedDrug));

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.deleteDrug(Collections.singletonMap("id", drugId.toHexString()), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDrugNotFound() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 drugService 的行为（未找到要删除的药品）
        ObjectId drugId = new ObjectId(); // 模拟药品ID
        when(drugService.deleteDrugById(drugId)).thenThrow(new IdNotFoundException("")); // 模拟添加药品失败


        // 调用被测试的方法
        ResponseEntity<String> response = drugController.deleteDrug(Collections.singletonMap("id", drugId.toHexString()), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDrugUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.deleteDrug(Collections.singletonMap("id", "someDrugId"), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDrugNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.deleteDrug(Collections.singletonMap("id", "someDrugId"), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testUpdateDrugSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DrugService 的行为（成功更新药品）
        Drug updatedDrug = new Drug(); // 模拟更新后的药品对象
        when(drugService.updateDrug(any(Drug.class))).thenReturn(Optional.of(updatedDrug));

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.updateDrug(new Drug(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDrugNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DrugService 的行为（未找到要更新的药品）
        when(drugService.updateDrug(any(Drug.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.updateDrug(new Drug(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDrugUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.updateDrug(new Drug(), session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDrugNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.updateDrug(new Drug(), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindDrugByNameSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DrugService 的行为（成功查找药品）
        List<Drug> drugs = new ArrayList<>();
        Drug drug1 = new Drug();
        Drug drug2 = new Drug();
        drug2.setName("Drug2");
        drug1.setName("Drug1");
        drugs.add(drug1);
        drugs.add(drug2);

        when(drugService.findDrugsByNameLike(anyString())).thenReturn(drugs);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findDrugByName("Drug", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String expectedResponseData = "{\"code\":200,\"data\":[{\"name\":\"Drug1\"},{\"name\":\"Drug2\"}],\"message\":\"获取药品信息成功\"}";
        assertEquals(expectedResponseData, response.getBody());
    }



    @Test
    public void testFindDrugByNameNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findDrugByName("Drug", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testFindAllDrugsSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DrugService 的行为（成功获取所有药品）
        List<Drug> drugs = new ArrayList<>(); // 模拟药品列表
        when(drugService.findAllDrugs()).thenReturn(drugs);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findAllDrugs(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // 这里可以添加进一步的验证，比如检查响应体中是否包含正确的药品信息等。
    }



    @Test
    public void testFindAllDrugsNotLoggedIn() {
        // 模拟会话中没有用户权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findAllDrugs(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testFindDrugByIdSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DrugService 的行为（成功找到药品）
        Drug drug = new Drug(); // 模拟找到的药品对象
        when(drugService.findDrugById(any(ObjectId.class))).thenReturn(Optional.of(drug));

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findDrugById("5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindDrugByIdNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟 DrugService 的行为（未找到要更新的药品）
        when(drugService.findDrugById(any(ObjectId.class))).thenThrow(new IdNotFoundException("")); // 模拟添加药品失败


        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findDrugById("5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }



    @Test
    public void testFindDrugByIdNotLoggedIn() {
        // 模拟会话中没有用户权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.findDrugById("5fc73dfac116601124123456", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadPicSuccess() throws IOException {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Drug 对象和 FileStorageService 的行为
        Drug drug = new Drug(); // 创建一个药品对象
        when(drugService.findDrugById(any())).thenReturn(Optional.of(drug)); // 模拟找到药品
        when(fileStorageService.storeDrugPic(any(), anyString())).thenReturn("file/path/pic.jpg"); // 模拟文件存储服务

        // 模拟调用方法传递的参数
        MockMultipartFile file = new MockMultipartFile("file", "pic.jpg", "image/jpeg", "mock image".getBytes());

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.uploadPic(file, "5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUploadPicNotFound() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DrugService 的行为（未找到要更新的药品）
        when(drugService.findDrugById(any())).thenReturn(Optional.empty());

        // 模拟调用方法传递的参数
        MockMultipartFile file = new MockMultipartFile("file", "pic.jpg", "image/jpeg", "mock image".getBytes());

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.uploadPic(file, "5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadPicUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");
        when(session.getAttribute("userId")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = drugController.uploadPic(null, "5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadPicNotLoggedIn() {
        // 模拟会话中的非登录
        when(session.getAttribute("userId")).thenReturn(null);


        // 调用被测试的方法
        ResponseEntity<String> response = drugController.uploadPic(null, "5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}
