package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.TourTheme;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 회원 가입 요청 DTO.
 */
public record UserCreateRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        TourTheme preferredTheme
) {
}
