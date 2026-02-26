package com.tunely.controller;

import com.tunely.dto.request.CourseCreateRequest;
import com.tunely.dto.response.CourseResponse;
import com.tunely.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody CourseCreateRequest request
    ) {
        CourseResponse response = courseService.createCourse(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
        @RequestHeader("X-User-Id") String userId
    ) {
        List<CourseResponse> courses = courseService.getMyCourses(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
}
