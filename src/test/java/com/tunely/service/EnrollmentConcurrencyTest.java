package com.tunely.service;

import com.tunely.domain.entity.Course;
import com.tunely.repository.CourseRepository;
import com.tunely.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EnrollmentConcurrencyTest {

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
        course.setCreatedBy("test-user");
        course.setCourseName("데이터베이스");
        course.setCapacity(50);
        course.setEnrolled(0);
        testCourse = courseRepository.save(course);
    }

    @Test
    @DisplayName("동시에 100명이 수강신청하면 정원 50명을 초과하지 않는다")
    void testConcurrentEnrollmentDoesNotExceedCapacity() throws InterruptedException {
        int threadCount = 100;
        int coreCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(coreCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String userId = "user-" + UUID.randomUUID();
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    enrollmentService.enroll(userId, testCourse.getId());
                } catch (Exception e) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        Thread.sleep(100);
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        int actualEnrolled = enrollmentRepository.countByCourseId(testCourse.getId());
        Course updatedCourse = courseRepository.findById(testCourse.getId()).orElseThrow();

        System.out.println("실제 신청 인원: " + actualEnrolled + "명");
        System.out.println("Course.enrolled: " + updatedCourse.getEnrolled() + "명");

        assertThat(actualEnrolled).isLessThanOrEqualTo(50);
    }
}
