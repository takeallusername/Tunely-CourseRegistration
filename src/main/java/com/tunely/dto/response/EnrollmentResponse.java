package com.tunely.dto.response;

import com.tunely.domain.entity.Enrollment;
import com.tunely.domain.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private boolean success;
    private String message;
    private Long enrollmentId;
    private Long courseId;
    private String courseName;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;

    public static EnrollmentResponse success(Enrollment enrollment) {
        return new EnrollmentResponse(
            true,
            "수강신청 성공",
            enrollment.getId(),
            enrollment.getCourse().getId(),
            enrollment.getCourse().getCourseName(),
            enrollment.getStatus(),
            enrollment.getEnrolledAt()
        );
    }

    public static EnrollmentResponse failure(String message) {
        return new EnrollmentResponse(
            false,
            message,
            null,
            null,
            null,
            EnrollmentStatus.FAILED,
            null
        );
    }

    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
            true,
            null,
            enrollment.getId(),
            enrollment.getCourse().getId(),
            enrollment.getCourse().getCourseName(),
            enrollment.getStatus(),
            enrollment.getEnrolledAt()
        );
    }
}
