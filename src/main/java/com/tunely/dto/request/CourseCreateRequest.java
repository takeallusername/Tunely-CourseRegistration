package com.tunely.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseCreateRequest {

    @NotBlank(message = "과목명은 필수입니다")
    private String courseName;

    @NotNull(message = "정원은 필수입니다")
    @Min(value = 1, message = "정원은 최소 1명 이상이어야 합니다")
    private Integer capacity;
}
