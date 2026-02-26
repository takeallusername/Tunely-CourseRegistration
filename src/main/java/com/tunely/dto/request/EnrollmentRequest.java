package com.tunely.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnrollmentRequest {

    @NotNull(message = "과목 ID는 필수입니다")
    private Long courseId;
}
