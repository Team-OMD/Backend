package com.onmydesk.backend.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}