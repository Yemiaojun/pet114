package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Role;
import com.example.wechat.service.RoleService;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleControllerTests {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @Mock
    private HttpSession session;

    @Test
    public void testDeleteRoleSuccess() {
        // 模拟会话中有用户ID和管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的deleteRoleById方法成功
        Role role = new Role();
        when(roleService.deleteRoleById(any(ObjectId.class))).thenReturn(Optional.of(role));

        // 构造请求体
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "some_id");

        ResponseEntity<String> responseEntity = roleController.deleteRole(payload, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("角色删除成功"));
    }

    @Test
    public void testDeleteRoleFailureUnauthorized() {
        // 模拟会话中没有用户ID和管理员权限
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 构造请求体
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "some_id");

        ResponseEntity<String> responseEntity = roleController.deleteRole(payload, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("用户未登录或不具备添加权限"));
    }


    @Test
    public void testUpdateRoleSuccess() {
        // 模拟会话中有管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的updateRole方法成功
        Role role = new Role();
        when(roleService.updateRole(any(Role.class))).thenReturn(Optional.of(role));

        ResponseEntity<String> responseEntity = roleController.updateRole(role, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("角色更新成功"));
    }

    @Test
    public void testUpdateRoleFailureUnauthorized() {
        // 模拟会话中没有管理员权限
        when(session.getAttribute("authLevel")).thenReturn(null);

        Role role = new Role();
        ResponseEntity<String> responseEntity = roleController.updateRole(role, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("用户未登录或不具备更新权限"));
    }


    @Test
    public void testAddRoleSuccess() {
        // 模拟会话中有用户ID和管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的addRole方法成功
        Role role = new Role();
        when(roleService.addRole(any(Role.class))).thenReturn(role);

        ResponseEntity<String> responseEntity = roleController.addRole(role, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("角色添加成功"));
    }

    @Test
    public void testAddRoleFailureUnauthorized() {
        // 模拟会话中没有用户ID和管理员权限
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute("authLevel")).thenReturn(null);

        Role role = new Role();
        ResponseEntity<String> responseEntity = roleController.addRole(role, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("用户未登录或不具备添加权限"));
    }

    @Test
    public void testAddRoleFailureServiceException() {
        // 模拟会话中有用户ID和管理员权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的addRole方法抛出异常
        when(roleService.addRole(any(Role.class))).thenThrow(new NameAlreadyExistedException("角色名已存在"));

        Role role = new Role();
        ResponseEntity<String> responseEntity = roleController.addRole(role, session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("角色名已存在"));
    }


    @Test
    public void testFindAllRolesSuccess() {
        // 模拟会话中有管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的findAllRoles方法成功
        List<Role> roles = new ArrayList<>();
        when(roleService.findAllRoles()).thenReturn(roles);

        ResponseEntity<String> responseEntity = roleController.findAllRoles(session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("获取所有角色信息成功"));
    }

    @Test
    public void testFindAllRolesFailureUnauthorized() {
        // 模拟会话中没有管理员权限
        when(session.getAttribute("authLevel")).thenReturn(null);

        ResponseEntity<String> responseEntity = roleController.findAllRoles(session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("用户未登录或不具备查看权限"));
    }



    @Test
    public void testFindRoleByIdSuccess() {
        // 模拟会话中有管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟RoleService的findRoleById方法成功
        Role role = new Role();
        when(roleService.findRoleById(any(ObjectId.class))).thenReturn(Optional.of(role));

        ResponseEntity<String> responseEntity = roleController.findRoleById("some_id", session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("获取角色信息成功"));
    }

    @Test
    public void testFindRoleByIdFailureUnauthorized() {
        // 模拟会话中没有管理员权限
        when(session.getAttribute("authLevel")).thenReturn(null);

        ResponseEntity<String> responseEntity = roleController.findRoleById("some_id", session);

        // 验证返回结果是否符合预期
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().contains("用户未登录或不具备查看权限"));
    }
}

