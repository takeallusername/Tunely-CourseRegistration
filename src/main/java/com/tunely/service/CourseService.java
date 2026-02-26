package com.tunely.service;

import com.tunely.domain.entity.Course;
import com.tunely.dto.request.CourseCreateRequest;
import com.tunely.dto.response.CourseResponse;
import com.tunely.exception.CustomExceptions.CourseNotFoundException;
import com.tunely.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public CourseResponse createCourse(String userId, CourseCreateRequest request) {
        Course course = new Course();
        course.setCreatedBy(userId);
        course.setCourseName(request.getCourseName());
        course.setCapacity(request.getCapacity());
        course.setEnrolled(0);

        Course saved = courseRepository.save(course);
        return CourseResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(String userId) {
        return courseRepository.findByCreatedBy(userId).stream()
            .map(CourseResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(CourseResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(CourseNotFoundException::new);
        return CourseResponse.from(course);
    }
}
