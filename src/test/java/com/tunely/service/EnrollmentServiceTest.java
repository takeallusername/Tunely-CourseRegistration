package com.tunely.service;

import com.tunely.domain.entity.Course;
import com.tunely.dto.response.EnrollmentResponse;
import com.tunely.exception.CustomExceptions.AlreadyEnrolledException;
import com.tunely.exception.CustomExceptions.CourseFullException;
import com.tunely.exception.CustomExceptions.CourseNotFoundException;
import com.tunely.exception.CustomExceptions.EnrollmentNotFoundException;
import com.tunely.exception.CustomExceptions.InvalidUserIdException;
import com.tunely.repository.CourseRepository;
import com.tunely.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();

        Course course = new Course();
        course.setCreatedBy("instructor-1");
        course.setCourseName("데이터베이스");
        course.setCapacity(50);
        course.setEnrolled(0);
        testCourse = courseRepository.save(course);
    }

    @Test
    @DisplayName("수강신청을 할 수 있다")
    void enroll() {
        EnrollmentResponse response = enrollmentService.enroll("user-1", testCourse.getId());

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("수강신청 성공");
        assertThat(response.getCourseId()).isEqualTo(testCourse.getId());
        assertThat(response.getCourseName()).isEqualTo("데이터베이스");
    }

    @Test
    @DisplayName("존재하지 않는 과목에 수강신청하면 예외가 발생한다")
    void enrollCourseNotFound() {
        assertThatThrownBy(() -> enrollmentService.enroll("user-1", 999L))
            .isInstanceOf(CourseNotFoundException.class)
            .hasMessage("과목을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("같은 과목에 중복 수강신청하면 예외가 발생한다")
    void enrollAlreadyEnrolled() {
        enrollmentService.enroll("user-1", testCourse.getId());

        assertThatThrownBy(() -> enrollmentService.enroll("user-1", testCourse.getId()))
            .isInstanceOf(AlreadyEnrolledException.class)
            .hasMessage("이미 수강신청한 과목입니다");
    }

    @Test
    @DisplayName("정원이 가득 찬 과목에 수강신청하면 예외가 발생한다")
    void enrollCourseFull() {
        testCourse.setEnrolled(50);
        courseRepository.save(testCourse);

        assertThatThrownBy(() -> enrollmentService.enroll("user-1", testCourse.getId()))
            .isInstanceOf(CourseFullException.class)
            .hasMessage("수강 정원이 가득 찼습니다");
    }

    @Test
    @DisplayName("내 수강신청 목록을 조회할 수 있다")
    void getMyEnrollments() {
        Course course1 = new Course();
        course1.setCreatedBy("instructor-1");
        course1.setCourseName("알고리즘");
        course1.setCapacity(30);
        course1.setEnrolled(0);
        course1 = courseRepository.save(course1);

        enrollmentService.enroll("user-1", testCourse.getId());
        enrollmentService.enroll("user-1", course1.getId());

        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments("user-1");

        assertThat(enrollments).hasSize(2);
    }

    @Test
    @DisplayName("수강신청을 취소할 수 있다")
    void cancelEnrollment() {
        EnrollmentResponse enrolled = enrollmentService.enroll("user-1", testCourse.getId());

        Course before = courseRepository.findById(testCourse.getId()).orElseThrow();
        assertThat(before.getEnrolled()).isEqualTo(1);

        enrollmentService.cancelEnrollment("user-1", enrolled.getEnrollmentId());

        Course after = courseRepository.findById(testCourse.getId()).orElseThrow();
        assertThat(after.getEnrolled()).isEqualTo(0);
        assertThat(enrollmentRepository.findById(enrolled.getEnrollmentId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 수강신청을 취소하면 예외가 발생한다")
    void cancelEnrollmentNotFound() {
        assertThatThrownBy(() -> enrollmentService.cancelEnrollment("user-1", 999L))
            .isInstanceOf(EnrollmentNotFoundException.class)
            .hasMessage("수강신청 내역을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("다른 사용자의 수강신청을 취소하면 예외가 발생한다")
    void cancelEnrollmentInvalidUser() {
        EnrollmentResponse enrolled = enrollmentService.enroll("user-1", testCourse.getId());

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment("user-2", enrolled.getEnrollmentId()))
            .isInstanceOf(InvalidUserIdException.class)
            .hasMessage("유효하지 않은 사용자 ID입니다");
    }
}
