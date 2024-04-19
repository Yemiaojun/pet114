package com.example.wechat.controller;



import com.example.wechat.model.Activity;
import com.example.wechat.service.ActivityService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityControllerTests {

    @Mock
    private ActivityService activityService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ActivityController activityController;

    @Test
    public void testAddActivitySuccess() {
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Activity activity = new Activity();
        when(activityService.addActivity(any(Activity.class))).thenReturn(activity);

        ResponseEntity<String> response = activityController.addActivity(activity, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddActivityFailure() {
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Activity activity = new Activity();
        when(activityService.addActivity(any(Activity.class))).thenThrow(new RuntimeException("Failed to add activity"));

        ResponseEntity<String> response = activityController.addActivity(activity, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteActivitySuccess() {
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Map<String, String> payload = new HashMap<>();
        payload.put("id", "validActivityId");

        Optional<Activity> optionalActivity = Optional.of(new Activity());
        when(activityService.deleteActivityById(any())).thenReturn(optionalActivity);

        ResponseEntity<String> response = activityController.deleteAssay(payload, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteActivityFailure() {
        when(session.getAttribute("userId")).thenReturn("1");
        when(session.getAttribute("authLevel")).thenReturn("2");

        Map<String, String> payload = new HashMap<>();
        payload.put("id", "validActivityId");

        when(activityService.deleteActivityById(any())).thenThrow(new RuntimeException("Failed to delete activity"));

        ResponseEntity<String> response = activityController.deleteAssay(payload, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateActivitySuccess() {
        when(session.getAttribute("authLevel")).thenReturn("2");

        Activity activity = new Activity();
        when(activityService.updateActivity(any(Activity.class))).thenReturn(Optional.of(activity));

        ResponseEntity<String> response = activityController.updateActivity(activity, session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateActivityNotFound() {
        when(session.getAttribute("authLevel")).thenReturn("2");

        Activity activity = new Activity();
        when(activityService.updateActivity(any(Activity.class))).thenReturn(Optional.empty());

        ResponseEntity<String> response = activityController.updateActivity(activity, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateActivityUnauthorized() {
        when(session.getAttribute("authLevel")).thenReturn("1");

        Activity activity = new Activity();

        ResponseEntity<String> response = activityController.updateActivity(activity, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateActivityNotLoggedIn() {
        when(session.getAttribute("authLevel")).thenReturn(null);

        Activity activity = new Activity();

        ResponseEntity<String> response = activityController.updateActivity(activity, session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testFindActivityByNameSuccess() {
        when(session.getAttribute("userId")).thenReturn("1");

        Optional<Activity> activity = Optional.of(new Activity());
        when(activityService.findActivityByName(anyString())).thenReturn(activity);

        ResponseEntity<String> response = activityController.findActivityByName("testName", session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindActivityByNameNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<String> response = activityController.findActivityByName("testName", session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    // Add more tests for other methods as needed
}
