package com.tunely.dto.response;

import com.tunely.domain.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String createdBy;
    private String courseName;
    private Integer capacity;
    private Integer enrolled;
    private LocalDateTime createdAt;

    public static CourseResponse from(Course course) {
        return new CourseResponse(
            course.getId(),
            course.getCreatedBy(),
            course.getCourseName(),
            course.getCapacity(),
            course.getEnrolled(),
            course.getCreatedAt()
        );
    }
}
