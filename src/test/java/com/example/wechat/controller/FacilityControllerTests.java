package com.example.wechat.controller;

import com.example.wechat.exception.NameAlreadyExistedException;
import com.example.wechat.model.Facility;
import com.example.wechat.service.FacilityService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacilityControllerTests {

    @Mock
    private FacilityService facilityService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private FacilityController facilityController;

    @Test
    public void testAddFacilitySuccess() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 Facility 对象和 FacilityService 的行为
        Facility facility = new Facility(); // 创建一个设备对象
        when(facilityService.addFacility(any(Facility.class))).thenReturn(facility); // 模拟添加设备成功

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.addFacility(facility, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddFacilityFailure() {
        // 模拟会话中的用户ID和权限
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 模拟 Facility 对象和 FacilityService 的行为
        Facility facility = new Facility(); // 创建一个设备对象
        when(facilityService.addFacility(any(Facility.class))).thenThrow(new NameAlreadyExistedException("名字已存在")); // 模拟添加设备失败

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.addFacility(facility, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // 每个测试方法中包含一个或多个测试用例，例如：



    // 更新设备信息接口的单元测试
    @Test
    public void testUpdateFacilitySuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 FacilityService 的行为（成功更新设备）
        Facility updatedFacility = new Facility(); // 模拟更新后的设备对象
        when(facilityService.updateFacility(any(Facility.class))).thenReturn(Optional.of(updatedFacility));

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.updateFacility(new Facility(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateFacilityNotFound() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 FacilityService 的行为（未找到要更新的设备）
        when(facilityService.updateFacility(any(Facility.class))).thenReturn(Optional.empty());

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.updateFacility(new Facility(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateFacilityUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.updateFacility(new Facility(), session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateFacilityNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.updateFacility(new Facility(), session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    // 根据设备名称模糊查询设备信息接口的单元测试
    @Test
    public void testFindFacilityByNameSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 FacilityService 的行为（成功查询设备）
        List<Facility> facilities = new ArrayList<>(); // 模拟查询到的设备列表
        when(facilityService.findFacilitiesByNameLike(anyString())).thenReturn(facilities);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityByName("testName", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindFacilityByNameUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityByName("testName", session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindFacilityByNameNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityByName("testName", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // 获取所有设备信息接口的单元测试
    @Test
    public void testFindAllFacilitiesSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 FacilityService 的行为（成功查询所有设备）
        List<Facility> facilities = new ArrayList<>(); // 模拟查询到的所有设备列表
        when(facilityService.findAllFacilities()).thenReturn(facilities);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findAllFacilities(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindAllFacilitiesUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findAllFacilities(session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindAllFacilitiesNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findAllFacilities(session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // 根据设备ID查询设备信息接口的单元测试
    @Test
    public void testFindFacilityByIdSuccess() {
        // 模拟会话中的管理员权限
        when(session.getAttribute("authLevel")).thenReturn("2");

        // 模拟 FacilityService 的行为（成功查询设备）
        Optional<Facility> facility = Optional.of(new Facility()); // 模拟查询到的设备
        when(facilityService.findFacilityById(any(ObjectId.class))).thenReturn(facility);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityById("testId", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindFacilityByIdUnauthorized() {
        // 模拟会话中的非管理员权限
        when(session.getAttribute("authLevel")).thenReturn("1");

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityById("testId", session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindFacilityByIdNotLoggedIn() {
        // 模拟会话中没有用户ID和权限信息（未登录）
        when(session.getAttribute("authLevel")).thenReturn(null);

        // 调用被测试的方法
        ResponseEntity<String> response = facilityController.findFacilityById("testId", session);

        // 验证返回的响应是否正确
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}

