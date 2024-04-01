package com.onmydesk.backend.user;

import com.onmydesk.backend.user.domain.Authority;
import com.onmydesk.backend.user.domain.User;
import com.onmydesk.backend.user.dto.UserRequest;
import com.onmydesk.backend.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRequest request){

        Authority authority = Authority.builder().
                authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .activated(true)
                .authorities(Collections.singleton(authority))
                .build();

        return user;
    }

    public UserResponse toResponse(User user){
        if(user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();
    }
}
