package com.onmydesk.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MemberResponse {
    private Long id;
    private String name;
    private String nickname;
    private String email;
}

