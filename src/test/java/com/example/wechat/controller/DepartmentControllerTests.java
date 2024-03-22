package com.example.wechat.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Department;
import com.example.wechat.service.DepartmentService;
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
public class DepartmentControllerTests {

    @Mock
    private DepartmentService departmentService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DepartmentController departmentController;

    @Test
    public void testAddDepartmentSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Department 对象和 DepartmentService 的行为
        Department department = new Department(); // 创建一个部门对象
        when(departmentService.addDepartment(any(Department.class))).thenReturn(department); // 模拟添加部门成功

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(department, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void testAddDepartmentFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 模拟 Department 对象和 DepartmentService 的行为
        Department department = new Department(); // 创建一个部门对象
        when(departmentService.addDepartment(any(Department.class))).thenThrow(new DefaultException("名字已存在")); // 模拟添加部门失败

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(department, session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentSuccess() {
        // 模拟会话中的用户ID和权限（管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DepartmentService 的行为（成功删除部门）
        String departmentName = "Test Department";
        when(departmentService.deleteDepartmentByName(departmentName)).thenReturn(Optional.of(new Department()));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(departmentName, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentNotFound() {
        // 模拟会话中的用户ID和权限（管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DepartmentService 的行为（未找到要删除的部门）
        String departmentName = "Nonexistent Department";
        when(departmentService.deleteDepartmentByName(departmentName)).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(departmentName, session);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentUnauthorized() {
        // 模拟会话中的用户ID和权限（非管理员权限）
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment("Any Department", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment("Any Department", session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DepartmentService 的行为（成功更新部门）
        Department updatedDepartment = new Department(); // 模拟更新后的部门对象
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(Optional.of(updatedDepartment));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 DepartmentService 的行为（未找到要更新的部门）
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

