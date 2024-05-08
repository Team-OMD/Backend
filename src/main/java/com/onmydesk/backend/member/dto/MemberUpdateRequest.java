package com.onmydesk.backend.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원이 정보를 수정할 때 필요한 데이터")
public class MemberUpdateRequest {
    @NotBlank
    @Schema(description = "사용자가 바꿀 이름", nullable = false, example = "최수하")
    private String name;

    @NotBlank
    @Schema(description = "사용자가 바꿀 닉네임", nullable = false, example = "귤")
    private String nickname;

}

