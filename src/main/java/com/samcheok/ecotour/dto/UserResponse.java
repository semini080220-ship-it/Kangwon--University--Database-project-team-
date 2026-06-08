package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.domain.User;

/**
 * 사용자 응답 DTO. 보유 스탬프 수(stampCount)는 VisitLog 에서 집계한 파생값.
 */
public record UserResponse(
        Long id,
        String email,
        String nickname,
        TourTheme preferredTheme,
        long stampCount
) {
    public static UserResponse of(User user, long stampCount) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPreferredTheme(),
                stampCount
        );
    }
}
