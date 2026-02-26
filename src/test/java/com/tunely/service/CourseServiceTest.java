package com.tunely.service;

import com.tunely.dto.request.CourseCreateRequest;
import com.tunely.dto.response.CourseResponse;
import com.tunely.exception.CustomExceptions.CourseNotFoundException;
import com.tunely.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("과목을 생성할 수 있다")
    void createCourse() {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseName("알고리즘");
        request.setCapacity(30);

        CourseResponse response = courseService.createCourse("user-1", request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreatedBy()).isEqualTo("user-1");
        assertThat(response.getCourseName()).isEqualTo("알고리즘");
        assertThat(response.getCapacity()).isEqualTo(30);
        assertThat(response.getEnrolled()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 과목을 조회할 수 있다")
    void getAllCourses() {
        CourseCreateRequest request1 = new CourseCreateRequest();
        request1.setCourseName("데이터베이스");
        request1.setCapacity(50);
        courseService.createCourse("user-1", request1);

        CourseCreateRequest request2 = new CourseCreateRequest();
        request2.setCourseName("운영체제");
        request2.setCapacity(40);
        courseService.createCourse("user-2", request2);

        List<CourseResponse> courses = courseService.getAllCourses();

        assertThat(courses).hasSize(2);
    }

    @Test
    @DisplayName("내가 만든 과목만 조회할 수 있다")
    void getMyCourses() {
        CourseCreateRequest request1 = new CourseCreateRequest();
        request1.setCourseName("데이터베이스");
        request1.setCapacity(50);
        courseService.createCourse("user-1", request1);

        CourseCreateRequest request2 = new CourseCreateRequest();
        request2.setCourseName("운영체제");
        request2.setCapacity(40);
        courseService.createCourse("user-1", request2);

        CourseCreateRequest request3 = new CourseCreateRequest();
        request3.setCourseName("알고리즘");
        request3.setCapacity(30);
        courseService.createCourse("user-2", request3);

        List<CourseResponse> myCourses = courseService.getMyCourses("user-1");

        assertThat(myCourses).hasSize(2);
        assertThat(myCourses).allMatch(course -> course.getCreatedBy().equals("user-1"));
    }

    @Test
    @DisplayName("ID로 과목을 조회할 수 있다")
    void getCourseById() {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setCourseName("네트워크");
        request.setCapacity(35);

        CourseResponse created = courseService.createCourse("user-1", request);
        CourseResponse found = courseService.getCourseById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getCourseName()).isEqualTo("네트워크");
    }

    @Test
    @DisplayName("존재하지 않는 과목을 조회하면 예외가 발생한다")
    void getCourseByIdNotFound() {
        assertThatThrownBy(() -> courseService.getCourseById(999L))
            .isInstanceOf(CourseNotFoundException.class)
            .hasMessage("과목을 찾을 수 없습니다");
    }
}
