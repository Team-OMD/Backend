package com.onmydesk.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
}
