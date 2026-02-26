package com.tunely.service;

import com.tunely.domain.entity.Course;
import com.tunely.domain.entity.Enrollment;
import com.tunely.domain.enums.EnrollmentStatus;
import com.tunely.dto.response.EnrollmentResponse;
import com.tunely.exception.CustomExceptions.*;
import com.tunely.repository.CourseRepository;
import com.tunely.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public EnrollmentResponse enroll(String userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(CourseNotFoundException::new);

        if (enrollmentRepository.existsByCourseIdAndUserId(courseId, userId)) {
            throw new AlreadyEnrolledException();
        }

        if (course.getEnrolled() >= course.getCapacity()) {
            throw new CourseFullException();
        }

        course.setEnrolled(course.getEnrolled() + 1);
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.SUCCESS);
        enrollmentRepository.save(enrollment);

        return EnrollmentResponse.success(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String userId) {
        return enrollmentRepository.findByUserId(userId).stream()
            .map(EnrollmentResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void cancelEnrollment(String userId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(EnrollmentNotFoundException::new);

        if (!enrollment.getUserId().equals(userId)) {
            throw new InvalidUserIdException();
        }

        Course course = enrollment.getCourse();
        course.setEnrolled(Math.max(0, course.getEnrolled() - 1));
        courseRepository.save(course);

        enrollmentRepository.delete(enrollment);
    }
}
