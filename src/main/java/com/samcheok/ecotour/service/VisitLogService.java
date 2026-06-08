package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.User;
import com.samcheok.ecotour.domain.VisitLog;
import com.samcheok.ecotour.domain.VisitType;
import com.samcheok.ecotour.dto.VisitLogCreateRequest;
import com.samcheok.ecotour.dto.VisitLogResponse;
import com.samcheok.ecotour.exception.DuplicateResourceException;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.UserRepository;
import com.samcheok.ecotour.repository.VisitLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 방문/인증 비즈니스 로직.
 * 규칙: 동일 관광지 스탬프는 1회만(중복 시 409), 친환경 활동(ECO_ACTIVITY)은 반복 인증 허용.
 */
@Service
@Transactional(readOnly = true)
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;

    public VisitLogService(VisitLogRepository visitLogRepository,
                           UserRepository userRepository,
                           AttractionRepository attractionRepository) {
        this.visitLogRepository = visitLogRepository;
        this.userRepository = userRepository;
        this.attractionRepository = attractionRepository;
    }

    @Transactional
    public VisitLogResponse create(VisitLogCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> ResourceNotFoundException.of("사용자", request.userId()));
        Attraction attraction = attractionRepository.findById(request.attractionId())
                .orElseThrow(() -> ResourceNotFoundException.of("관광지", request.attractionId()));

        if (request.visitType() == VisitType.STAMP
                && visitLogRepository.existsByUserIdAndAttractionIdAndVisitType(
                        user.getId(), attraction.getId(), VisitType.STAMP)) {
            throw new DuplicateResourceException("이미 스탬프를 찍은 관광지입니다.");
        }

        VisitLog saved = visitLogRepository.save(
                new VisitLog(user, attraction, request.visitType(), request.note()));
        return VisitLogResponse.from(saved);
    }

    public List<VisitLogResponse> getByUser(Long userId) {
        return visitLogRepository.findByUserId(userId).stream()
                .map(VisitLogResponse::from)
                .toList();
    }

    public long countStamps(Long userId) {
        return visitLogRepository.countByUserIdAndVisitType(userId, VisitType.STAMP);
    }
}
