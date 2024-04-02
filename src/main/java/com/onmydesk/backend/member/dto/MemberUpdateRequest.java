package com.onmydesk.backend.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
}

