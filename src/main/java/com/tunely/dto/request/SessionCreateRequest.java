package com.tunely.dto.request;

import com.tunely.domain.enums.DifficultyLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionCreateRequest {

    @NotNull(message = "시작까지 남은 시간(분)은 필수입니다")
    @Min(value = 1, message = "시작까지 남은 시간은 최소 1분 이상이어야 합니다")
    private Integer minutesUntilStart;

    @NotNull(message = "난이도는 필수입니다")
    private DifficultyLevel difficulty;
}
