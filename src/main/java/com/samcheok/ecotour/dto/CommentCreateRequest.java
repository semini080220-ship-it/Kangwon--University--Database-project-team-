package com.samcheok.ecotour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 댓글 작성 요청 DTO. 관광지 ID는 경로 변수로 받으므로 본문에는 작성자·내용만 담는다.
 */
public record CommentCreateRequest(

        @NotBlank(message = "작성자 닉네임은 필수입니다.")
        @Size(max = 50, message = "작성자 닉네임은 50자 이하여야 합니다.")
        String author,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 1000, message = "댓글 내용은 1000자 이하여야 합니다.")
        String content
) {
}
