package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Assay;
import com.example.wechat.service.AssayService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssayControllerTests {

    @Mock
    private AssayService assayService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AssayController assayController;

    @Test
    public void testAddAssaySuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Assay 对象和 AssayService 的行为
        Assay assay = new Assay();
        when(assayService.addAssay(any(Assay.class))).thenReturn(assay);

        // 调用被测试的方法
        ResponseEntity<String> response = assayController.addAssay(assay, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddAssayFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Assay 对象和 AssayService 的行为
        Assay assay = new Assay();
        when(assayService.addAssay(any(Assay.class))).thenThrow(new NameAlreadyExistedException("名称已存在"));

        // 调用被测试的方法
        ResponseEntity<String> response = assayController.addAssay(assay, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteAssaySuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 AssayService 的行为
        Assay assay = new Assay();
        when(assayService.deleteAssayById(any(ObjectId.class))).thenReturn(Optional.of(assay));

        // 准备请求体信息
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "validAssayId");

        // 调用被测试的方法
        ResponseEntity<String> response = assayController.deleteAssay(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteAssayFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 AssayService 的行为
        when(assayService.deleteAssayById(any(ObjectId.class))).thenReturn(Optional.empty());

        // 准备请求体信息
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "invalidAssayId");

        // 调用被测试的方法
        ResponseEntity<String> response = assayController.deleteAssay(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}
