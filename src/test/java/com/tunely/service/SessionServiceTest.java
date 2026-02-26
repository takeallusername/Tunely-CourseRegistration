package com.tunely.service;

import com.tunely.domain.enums.DifficultyLevel;
import com.tunely.dto.request.SessionCreateRequest;
import com.tunely.dto.response.SessionResponse;
import com.tunely.exception.CustomExceptions.SessionAlreadyExistsException;
import com.tunely.exception.CustomExceptions.SessionNotFoundException;
import com.tunely.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static java.time.temporal.ChronoUnit.SECONDS;

@SpringBootTest
class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("세션을 생성할 수 있다")
    void createSession() {
        SessionCreateRequest request = new SessionCreateRequest();
        request.setMinutesUntilStart(10);
        request.setDifficulty(DifficultyLevel.MEDIUM);

        SessionResponse response = sessionService.createSession("user-1", request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user-1");
        assertThat(response.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
        assertThat(response.getVirtualUsers()).isEqualTo(500);
        assertThat(response.getStartTime()).isCloseTo(LocalDateTime.now().plusMinutes(10), within(1, SECONDS));
    }

    @Test
    @DisplayName("진행 중인 세션이 있으면 새로운 세션을 만들 수 없다")
    void createSessionAlreadyExists() {
        SessionCreateRequest request1 = new SessionCreateRequest();
        request1.setMinutesUntilStart(10);
        request1.setDifficulty(DifficultyLevel.EASY);
        sessionService.createSession("user-1", request1);

        SessionCreateRequest request2 = new SessionCreateRequest();
        request2.setMinutesUntilStart(20);
        request2.setDifficulty(DifficultyLevel.HARD);

        assertThatThrownBy(() -> sessionService.createSession("user-1", request2))
            .isInstanceOf(SessionAlreadyExistsException.class)
            .hasMessage("이미 진행 중인 세션이 있습니다");
    }

    @Test
    @DisplayName("시작 시간이 지난 세션이 있어도 새로운 세션을 만들 수 있다")
    void createSessionAfterPreviousEnded() {
        SessionCreateRequest request1 = new SessionCreateRequest();
        request1.setMinutesUntilStart(1);
        request1.setDifficulty(DifficultyLevel.EASY);
        SessionResponse first = sessionService.createSession("user-1", request1);

        sessionRepository.findById(first.getId()).ifPresent(session -> {
            session.setStartTime(LocalDateTime.now().minusMinutes(10));
            sessionRepository.save(session);
        });

        SessionCreateRequest request2 = new SessionCreateRequest();
        request2.setMinutesUntilStart(5);
        request2.setDifficulty(DifficultyLevel.HARD);
        SessionResponse second = sessionService.createSession("user-1", request2);

        assertThat(second.getId()).isNotEqualTo(first.getId());
    }

    @Test
    @DisplayName("내 모든 세션을 조회할 수 있다")
    void getAllMySessions() {
        SessionCreateRequest request1 = new SessionCreateRequest();
        request1.setMinutesUntilStart(1);
        request1.setDifficulty(DifficultyLevel.EASY);
        SessionResponse first = sessionService.createSession("user-1", request1);

        sessionRepository.findById(first.getId()).ifPresent(session -> {
            session.setStartTime(LocalDateTime.now().minusMinutes(10));
            sessionRepository.save(session);
        });

        SessionCreateRequest request2 = new SessionCreateRequest();
        request2.setMinutesUntilStart(5);
        request2.setDifficulty(DifficultyLevel.MEDIUM);
        sessionService.createSession("user-1", request2);

        List<SessionResponse> sessions = sessionService.getAllMySessions("user-1");

        assertThat(sessions).hasSize(2);
    }

    @Test
    @DisplayName("다른 사용자의 세션은 조회되지 않는다")
    void getAllMySessionsOnlyMine() {
        SessionCreateRequest request1 = new SessionCreateRequest();
        request1.setMinutesUntilStart(10);
        request1.setDifficulty(DifficultyLevel.EASY);
        sessionService.createSession("user-1", request1);

        SessionCreateRequest request2 = new SessionCreateRequest();
        request2.setMinutesUntilStart(20);
        request2.setDifficulty(DifficultyLevel.HARD);
        sessionService.createSession("user-2", request2);

        List<SessionResponse> user1Sessions = sessionService.getAllMySessions("user-1");

        assertThat(user1Sessions).hasSize(1);
        assertThat(user1Sessions.get(0).getUserId()).isEqualTo("user-1");
    }

    @Test
    @DisplayName("ID로 세션을 조회할 수 있다")
    void getSessionById() {
        SessionCreateRequest request = new SessionCreateRequest();
        request.setMinutesUntilStart(15);
        request.setDifficulty(DifficultyLevel.VERY_HARD);

        SessionResponse created = sessionService.createSession("user-1", request);
        SessionResponse found = sessionService.getSessionById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getDifficulty()).isEqualTo(DifficultyLevel.VERY_HARD);
    }

    @Test
    @DisplayName("존재하지 않는 세션을 조회하면 예외가 발생한다")
    void getSessionByIdNotFound() {
        assertThatThrownBy(() -> sessionService.getSessionById(999L))
            .isInstanceOf(SessionNotFoundException.class)
            .hasMessage("세션을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("세션을 삭제할 수 있다")
    void deleteSession() {
        SessionCreateRequest request = new SessionCreateRequest();
        request.setMinutesUntilStart(10);
        request.setDifficulty(DifficultyLevel.MEDIUM);

        SessionResponse created = sessionService.createSession("user-1", request);
        sessionService.deleteSession(created.getId());

        assertThat(sessionRepository.findById(created.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 세션을 삭제하면 예외가 발생한다")
    void deleteSessionNotFound() {
        assertThatThrownBy(() -> sessionService.deleteSession(999L))
            .isInstanceOf(SessionNotFoundException.class)
            .hasMessage("세션을 찾을 수 없습니다");
    }
}
