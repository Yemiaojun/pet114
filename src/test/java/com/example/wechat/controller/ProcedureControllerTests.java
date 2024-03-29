package com.example.wechat.controller;

import com.example.wechat.model.Procedure;
import com.example.wechat.service.FileStorageService;
import com.example.wechat.service.ProcedureService;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import utils.Result;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProcedureControllerTests {

    @Mock
    private ProcedureService procedureService;

    @Mock
    private HttpSession session;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProcedureController procedureController;

    public ProcedureControllerTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddProcedureSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟流程服务的行为（成功添加流程）
        Procedure procedure = new Procedure();
        when(procedureService.addProcedure(any(Procedure.class))).thenReturn(procedure);

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.addProcedure(procedure, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("流程添加成功", procedure), response.getBody());
    }

    @Test
    public void testAddProcedureUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.addProcedure(null, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }

    @Test
    public void testAddProcedureUserNotAuthorized() {
        // 模拟会话中的用户登录但没有管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.addProcedure(null, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }

    @Test
    public void testDeleteProcedureSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟请求体中的流程ID
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "5fc73dfac116601124123456");

        // 模拟流程服务的行为（成功删除流程）
        Procedure procedure = new Procedure();
        when(procedureService.deleteProcedureById(any(ObjectId.class))).thenReturn(Optional.of(procedure));

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteProcedure(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("流程删除成功", Optional.of(procedure)), response.getBody());
    }

    @Test
    public void testDeleteProcedureUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 模拟请求体中的流程ID
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "5fc73dfac116601124123456");

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteProcedure(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }

    @Test
    public void testDeleteProcedureUserNotAuthorized() {
        // 模拟会话中的用户登录但没有管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 模拟请求体中的流程ID
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "5fc73dfac116601124123456");

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteProcedure(payload, session);
        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备添加权限"), response.getBody());
    }


    @Test
    public void testUpdateProcedureSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟流程服务的行为（成功更新流程）
        Procedure updatedProcedure = new Procedure();
        when(procedureService.updateProcedure(any(Procedure.class))).thenReturn(Optional.of(updatedProcedure));

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.updateProcedure(new Procedure(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("流程更新成功", updatedProcedure), response.getBody());
    }

    @Test
    public void testUpdateProcedureUserNotAuthorized() {
        // 模拟会话中的用户没有管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.updateProcedure(null, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录或不具备更新权限"), response.getBody());
    }

    @Test
    public void testFindProceduresByRoleIdSuccess() {
        // 模拟会话中的用户已登录
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟流程服务的行为（成功获取流程列表）
        ObjectId roleId = new ObjectId("5fc73dfac116601124123456");
        List<Procedure> procedures = Arrays.asList(new Procedure(), new Procedure());
        when(procedureService.findProcedureByRoleId(any(ObjectId.class))).thenReturn(procedures);

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.findProceduresByRoleId(roleId.toHexString(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("获取部流程息成功", procedures), response.getBody());
    }

    @Test
    public void testFindProceduresByRoleIdUserNotLoggedIn() {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.findProceduresByRoleId("5fc73dfac116601124123456", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }


    @Test
    public void testUploadPicSuccess() throws IOException {
        // 模拟会话中的用户已登录
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟上传的文件
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

        // 模拟上传文件的存储路径
        String filePath = "/uploads/test.jpg";
        when(fileStorageService.storeProcedurePic(any(), any())).thenReturn(filePath);

        // 模拟流程服务的行为（成功更新流程图片）
        ObjectId procedureId = new ObjectId("5fc73dfac116601124123456");
        when(procedureService.findProcedureById(any(ObjectId.class))).thenReturn(Optional.of(new Procedure()));

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.uploadResource(file, procedureId.toHexString(), "pic",session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("图片更新成功", filePath), response.getBody());
    }

    @Test
    public void testUploadPicUserNotLoggedIn() throws IOException {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.uploadResource(null, "123456789012345678901234","pic", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }

    @Test
    public void testDeleteResourcePicSuccess() throws IOException {
        // 模拟会话中的用户已登录
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟文件名和资源类型
        String fileName = "test.jpg";
        String type = "pic";

        // 模拟流程服务的行为（成功删除流程图片）
        ObjectId procedureId = new ObjectId("123456789012345678901234");
        when(procedureService.findProcedureById(any(ObjectId.class))).thenReturn(Optional.of(new Procedure()));

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteResource(fileName, procedureId.toHexString(), type, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("图片删除成功", fileName), response.getBody());
    }

    @Test
    public void testDeleteResourceVidSuccess() throws IOException {
        // 模拟会话中的用户已登录
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟文件名和资源类型
        String fileName = "test.mp4";
        String type = "vid";

        // 模拟流程服务的行为（成功删除流程视频）
        ObjectId procedureId = new ObjectId("123456789012345678901234");
        when(procedureService.findProcedureById(any(ObjectId.class))).thenReturn(Optional.of(new Procedure()));

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteResource(fileName, procedureId.toHexString(), type, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Result.okGetStringByData("视频删除成功", fileName), response.getBody());
    }

    @Test
    public void testDeleteResourceUserNotLoggedIn() throws IOException {
        // 模拟会话中没有用户登录

        // 调用被测试的方法
        ResponseEntity<String> response = procedureController.deleteResource("test.jpg", "123456789012345678901234", "pic", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Result.errorGetString("用户未登录"), response.getBody());
    }
}

