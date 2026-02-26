package com.tunely.service;

import com.tunely.domain.entity.Session;
import com.tunely.dto.request.SessionCreateRequest;
import com.tunely.dto.response.SessionResponse;
import com.tunely.exception.CustomExceptions.SessionAlreadyExistsException;
import com.tunely.exception.CustomExceptions.SessionNotFoundException;
import com.tunely.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public SessionResponse createSession(String userId, SessionCreateRequest request) {
        List<Session> activeSessions = sessionRepository.findByUserIdAndStartTimeAfter(userId, LocalDateTime.now());
        if (!activeSessions.isEmpty()) {
            throw new SessionAlreadyExistsException();
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setStartTime(LocalDateTime.now().plusMinutes(request.getMinutesUntilStart()));
        session.setDifficulty(request.getDifficulty());

        Session saved = sessionRepository.save(session);
        return SessionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getAllMySessions(String userId) {
        return sessionRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(SessionResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
            .orElseThrow(SessionNotFoundException::new);
        return SessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
            .orElseThrow(SessionNotFoundException::new);
        sessionRepository.delete(session);
    }
}
