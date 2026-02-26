package com.tunely.controller;

import com.tunely.dto.request.EnrollmentRequest;
import com.tunely.dto.response.EnrollmentResponse;
import com.tunely.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody EnrollmentRequest request
    ) {
        EnrollmentResponse response = enrollmentService.enroll(userId, request.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
        @RequestHeader("X-User-Id") String userId
    ) {
        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(userId);
        return ResponseEntity.ok(enrollments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEnrollment(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long id
    ) {
        enrollmentService.cancelEnrollment(userId, id);
        return ResponseEntity.noContent().build();
    }
}
