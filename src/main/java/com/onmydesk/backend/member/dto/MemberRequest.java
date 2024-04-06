package com.onmydesk.backend.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원이 회원가입할때 필요한 데이터")
public class MemberRequest {

    @Email
    @NotBlank
    @Schema(description = "사용자 이메일", nullable = false, example = "jihye@naver.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 비밀번호", nullable = false, example = "qwe123")
    private String password;

    @NotBlank
    @Schema(description = "사용자 이름", nullable = false, example = "최지혜")
    private String name;

    @NotBlank
    @Schema(description = "사용자 닉네임", nullable = false, example = "최졔")
    private String nickname;
}

