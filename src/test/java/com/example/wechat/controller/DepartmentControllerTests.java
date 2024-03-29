package com.example.wechat.controller;

import com.example.wechat.exception.IdNotFoundException;
import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Department;
import com.example.wechat.service.DepartmentService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DepartmentControllerTests {

    @Mock
    private DepartmentService departmentService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DepartmentController departmentController;

    public DepartmentControllerTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddDepartmentSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟添加科室成功
        Department department = new Department();
        when(departmentService.addDepartment(any(Department.class))).thenReturn(department);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(department, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddDepartmentFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟添加科室失败
        Department department = new Department();
        when(departmentService.addDepartment(any(Department.class))).thenThrow(new NameAlreadyExistedException("科室名称已存在"));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(department, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddDepartmentUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddDepartmentNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.addDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentSuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟删除部门成功
        String departmentId = "5fc73dfac116601124123456";
        Map<String, String> payload = new HashMap<>();
        payload.put("id", departmentId);
        when(departmentService.deleteDepartmentById(any(ObjectId.class))).thenReturn(Optional.of(new Department()));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentNotFound() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟删除部门未找到
        String departmentId = "v5fc73dfac116601124123456";
        Map<String, String> payload = new HashMap<>();
        payload.put("id", departmentId);
        when(departmentService.deleteDepartmentById(any(ObjectId.class))).thenThrow(new IdNotFoundException("")); // 模拟添加药品失败

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(payload, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(new HashMap<>(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteDepartmentNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.deleteDepartment(new HashMap<>(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testUpdateDepartmentSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟更新部门成功
        Department department = new Department();
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(Optional.of(department));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(department, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟更新部门未找到
        Department department = new Department();
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(department, session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateDepartmentNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.updateDepartment(new Department(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindAllDepartmentsSuccess() {
        // 模拟会话中的用户ID
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟查找所有科室成功
        List<Department> departments = new ArrayList<>();
        Department department1 = new Department();
        department1.setName("Department 1");
        Department department2 = new Department();
        department2.setName("Department 2");
        departments.add(department1);
        departments.add(department2);
        when(departmentService.findAllDepartments()).thenReturn(departments);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findAllDepartments(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"code\":200,\"data\":[{\"name\":\"Department 1\"},{\"name\":\"Department 2\"}],\"message\":\"获取所有科室信息成功\"}", response.getBody());
    }

    @Test
    public void testFindAllDepartmentsNotLoggedIn() {
        // 模拟会话中没有用户ID（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findAllDepartments(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testFindDepartmentsByNameSuccess() {
        // 模拟会话中的用户ID
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟查找符合条件的科室成功
        List<Department> departments = new ArrayList<>();
        Department department1 = new Department();
        department1.setName("Department 1");
        Department department2 = new Department();
        department2.setName("Department 2");
        departments.add(department1);
        departments.add(department2);
        when(departmentService.findDepartmentsByNameLike(anyString())).thenReturn(departments);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findDepartmentsByName("Department", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"code\":200,\"data\":[{\"name\":\"Department 1\"},{\"name\":\"Department 2\"}],\"message\":\"获取部门信息成功\"}", response.getBody());
    }

    @Test
    public void testFindDepartmentsByNameNotLoggedIn() {
        // 模拟会话中没有用户ID（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findDepartmentsByName("Department", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindDepartmentByIdSuccess() {
        // 模拟会话中的用户ID
        when(session.getAttribute("userId")).thenReturn("1");

        // 模拟查找部门成功
        ObjectId id = new ObjectId();
        Department department = new Department();
        department.setName("Test Department");
        when(departmentService.findDepartmentById(id)).thenReturn(Optional.of(department));

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findDepartmentById(id.toHexString(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"code\":200,\"data\":{\"name\":\"Test Department\"},\"message\":\"获取部门信息成功\"}", response.getBody());
    }

    @Test
    public void testFindDepartmentByIdNotLoggedIn() {
        // 模拟会话中没有用户ID（未登录）
        when(session.getAttribute("userId")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = departmentController.findDepartmentById("validObjectId", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
