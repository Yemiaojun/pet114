package com.example.wechat.controller;
import com.example.wechat.model.Procedure;
import com.example.wechat.service.ProcedureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcedureControllerTests {

    @Mock
    private ProcedureService procedureService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ProcedureController procedureController;

    @Test
    void testAddProcedure_Success() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("123");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Procedure procedure = new Procedure(); // 构造一个流程对象
        when(procedureService.addProcedure(any())).thenReturn(procedure);

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.addProcedure(procedure, session);

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testAddProcedure_Failure() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("123");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Procedure procedure = new Procedure(); // 构造一个流程对象
        when(procedureService.addProcedure(any())).thenThrow(new RuntimeException("添加流程失败")); // 模拟添加失败

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.addProcedure(procedure, session);

        // Verify the response
        assertEquals(400, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testDeleteProcedure_Success() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("123");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "abc123");

        // Mock service response
        Procedure procedure = new Procedure();
        when(procedureService.deleteProcedureById(any())).thenReturn(Optional.of(procedure));

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.deleteProcedure(requestBody, session);

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testDeleteProcedure_Failure() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("123");
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("id", "abc123");

        // Mock service response
        when(procedureService.deleteProcedureById(any())).thenReturn(Optional.empty()); // 模拟删除失败

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.deleteProcedure(requestBody, session);

        // Verify the response
        assertEquals(400, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testUpdateProcedure_Success() {
        // Mock session attributes
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Procedure procedure = new Procedure(); // 构造一个流程对象

        // Mock service response
        when(procedureService.updateProcedure(any())).thenReturn(Optional.of(procedure));

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.updateProcedure(procedure, session);

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testUpdateProcedure_Failure() {
        // Mock session attributes
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request body
        Procedure procedure = new Procedure(); // 构造一个流程对象

        // Mock service response
        when(procedureService.updateProcedure(any())).thenReturn(Optional.empty()); // 模拟更新失败

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.updateProcedure(procedure, session);

        // Verify the response
        assertEquals(400, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testFindProceduresByFacilityId_Success() {
        // Mock session attributes
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request parameter
        String id = "abc123";

        // Mock service response
        List<Procedure> procedures = new ArrayList<>();
        procedures.add(new Procedure());
        procedures.add(new Procedure());
        when(procedureService.findProcedureByFacilityId(any())).thenReturn(procedures);

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.findProceduresByFacilityId(id, session);

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }

    @Test
    void testFindProceduresByFacilityId_Failure() {
        // Mock session attributes
        when(session.getAttribute("authLevel")).thenReturn("2");

        // Mock request parameter
        String id = "abc123";

        // Mock service response
        when(procedureService.findProcedureByFacilityId(any())).thenReturn(Collections.emptyList()); // 模拟查询失败

        // Call the controller method
        ResponseEntity<String> responseEntity = procedureController.findProceduresByFacilityId(id, session);

        // Verify the response
        assertEquals(400, responseEntity.getStatusCodeValue());
        // Add more assertions for the response body if needed
    }
}
