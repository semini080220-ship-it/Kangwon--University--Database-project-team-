package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.domain.TourTheme;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("VisitLogService 단위 테스트")
class VisitLogServiceTest {

    @Mock
    private VisitLogRepository visitLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AttractionRepository attractionRepository;
    @InjectMocks
    private VisitLogService visitLogService;

    private User user(Long id) {
        User u = new User("a@test.com", "관광객", TourTheme.ECO);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    private Attraction attraction(Long id, String name) {
        Attraction a = new Attraction(name, "d", Category.COAST, 37.0, 129.0, "삼척", CongestionLevel.LOW, true);
        ReflectionTestUtils.setField(a, "id", id);
        return a;
    }

    private void stubSaveWithId(long id) {
        when(visitLogRepository.save(any(VisitLog.class))).thenAnswer(inv -> {
            VisitLog v = inv.getArgument(0);
            ReflectionTestUtils.setField(v, "id", id);
            return v;
        });
    }

    @Test
    @DisplayName("스탬프 인증 성공")
    void create_stamp_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(visitLogRepository.existsByUserIdAndAttractionIdAndVisitType(1L, 10L, VisitType.STAMP))
                .thenReturn(false);
        stubSaveWithId(100L);

        VisitLogResponse res = visitLogService.create(
                new VisitLogCreateRequest(1L, 10L, VisitType.STAMP, "첫 방문"));

        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.attractionName()).isEqualTo("초곡");
        assertThat(res.visitType()).isEqualTo(VisitType.STAMP);
    }

    @Test
    @DisplayName("동일 관광지 스탬프 중복이면 DuplicateResourceException")
    void create_duplicateStamp() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(visitLogRepository.existsByUserIdAndAttractionIdAndVisitType(1L, 10L, VisitType.STAMP))
                .thenReturn(true);

        assertThatThrownBy(() -> visitLogService.create(
                new VisitLogCreateRequest(1L, 10L, VisitType.STAMP, null)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(visitLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("친환경 활동(ECO_ACTIVITY)은 중복 검사 없이 반복 인증 가능")
    void create_ecoActivity_allowed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        stubSaveWithId(101L);

        VisitLogResponse res = visitLogService.create(
                new VisitLogCreateRequest(1L, 10L, VisitType.ECO_ACTIVITY, "쓰레기 줍기"));

        assertThat(res.visitType()).isEqualTo(VisitType.ECO_ACTIVITY);
    }

    @Test
    @DisplayName("없는 사용자면 ResourceNotFoundException")
    void create_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> visitLogService.create(
                new VisitLogCreateRequest(99L, 10L, VisitType.STAMP, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("없는 관광지면 ResourceNotFoundException")
    void create_attractionNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> visitLogService.create(
                new VisitLogCreateRequest(1L, 99L, VisitType.STAMP, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("사용자의 보유 스탬프 수를 센다")
    void countStamps() {
        when(visitLogRepository.countByUserIdAndVisitType(1L, VisitType.STAMP)).thenReturn(5L);
        assertThat(visitLogService.countStamps(1L)).isEqualTo(5L);
    }

    @Test
    @DisplayName("사용자의 방문 기록을 조회한다")
    void getByUser() {
        User u = user(1L);
        VisitLog v = new VisitLog(u, attraction(10L, "초곡"), VisitType.STAMP, "n");
        ReflectionTestUtils.setField(v, "id", 1L);
        when(visitLogRepository.findByUserId(1L)).thenReturn(List.of(v));

        assertThat(visitLogService.getByUser(1L)).hasSize(1)
                .extracting(VisitLogResponse::attractionName).containsExactly("초곡");
    }
}
