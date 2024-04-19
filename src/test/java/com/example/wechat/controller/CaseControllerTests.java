package com.example.wechat.controller;

import com.example.wechat.controller.CaseController;
import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Case;
import com.example.wechat.service.CaseService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CaseControllerTests {

    @Mock
    private CaseService caseService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CaseController caseController;

    public CaseControllerTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddCase_Success() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Case newCase = new Case();
        // Mock service method
        when(caseService.addCase(newCase)).thenReturn(Optional.of(newCase));

        // Call controller method
        ResponseEntity<String> response = caseController.addCase(newCase, session);

        // Verify service method is called
        verify(caseService, times(1)).addCase(newCase);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testAddCase_Unauthorized() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("1");

        Case newCase = new Case();

        // Call controller method
        ResponseEntity<String> response = caseController.addCase(newCase, session);

        // Verify service method is not called
        verify(caseService, never()).addCase(newCase);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testAddCase_Exception() {
        // Mock session attributes
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Case newCase = new Case();
        // Mock service method to throw an exception
        when(caseService.addCase(newCase)).thenThrow(new DefaultException("Error"));

        // Call controller method
        ResponseEntity<String> response = caseController.addCase(newCase, session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error", response.getBody());
    }

    // Similarly, you can write tests for other controller methods
}
