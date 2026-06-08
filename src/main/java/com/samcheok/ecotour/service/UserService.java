package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.User;
import com.samcheok.ecotour.domain.VisitType;
import com.samcheok.ecotour.dto.UserCreateRequest;
import com.samcheok.ecotour.dto.UserResponse;
import com.samcheok.ecotour.exception.DuplicateResourceException;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.UserRepository;
import com.samcheok.ecotour.repository.VisitLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 비즈니스 로직. 이메일 중복 방지 + 보유 스탬프 수 집계.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final VisitLogRepository visitLogRepository;

    public UserService(UserRepository userRepository, VisitLogRepository visitLogRepository) {
        this.userRepository = userRepository;
        this.visitLogRepository = visitLogRepository;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다: " + request.email());
        }
        User saved = userRepository.save(
                new User(request.email(), request.nickname(), request.preferredTheme()));
        return UserResponse.of(saved, 0L);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("사용자", id));
        long stampCount = visitLogRepository.countByUserIdAndVisitType(id, VisitType.STAMP);
        return UserResponse.of(user, stampCount);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.of(user,
                        visitLogRepository.countByUserIdAndVisitType(user.getId(), VisitType.STAMP)))
                .toList();
    }
}
