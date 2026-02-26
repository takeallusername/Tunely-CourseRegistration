package com.tunely.dto.response;

import com.tunely.domain.entity.Session;
import com.tunely.domain.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private Long id;
    private String userId;
    private LocalDateTime startTime;
    private DifficultyLevel difficulty;
    private Integer virtualUsers;
    private LocalDateTime createdAt;

    public static SessionResponse from(Session session) {
        return new SessionResponse(
            session.getId(),
            session.getUserId(),
            session.getStartTime(),
            session.getDifficulty(),
            session.getDifficulty().getVirtualUsers(),
            session.getCreatedAt()
        );
    }
}
