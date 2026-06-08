package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.domain.User;
import com.samcheok.ecotour.domain.VisitType;
import com.samcheok.ecotour.dto.UserCreateRequest;
import com.samcheok.ecotour.dto.UserResponse;
import com.samcheok.ecotour.exception.DuplicateResourceException;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.UserRepository;
import com.samcheok.ecotour.repository.VisitLogRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VisitLogRepository visitLogRepository;
    @InjectMocks
    private UserService userService;

    private User user(Long id, String email) {
        User u = new User(email, "관광객", TourTheme.ECO);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    @Test
    @DisplayName("회원 가입 성공")
    void create_success() {
        UserCreateRequest req = new UserCreateRequest("a@test.com", "관광객", TourTheme.ECO);
        when(userRepository.existsByEmail("a@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 1L);
            return u;
        });

        UserResponse res = userService.create(req);

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.email()).isEqualTo("a@test.com");
        assertThat(res.stampCount()).isZero();
    }

    @Test
    @DisplayName("이메일 중복이면 DuplicateResourceException")
    void create_duplicateEmail() {
        when(userRepository.existsByEmail("a@test.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.create(new UserCreateRequest("a@test.com", "관광객", TourTheme.ECO)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("조회 시 보유 스탬프 수를 집계한다")
    void getById_withStampCount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "a@test.com")));
        when(visitLogRepository.countByUserIdAndVisitType(1L, VisitType.STAMP)).thenReturn(3L);

        UserResponse res = userService.getById(1L);

        assertThat(res.stampCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("없는 사용자 조회 시 ResourceNotFoundException")
    void getById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
