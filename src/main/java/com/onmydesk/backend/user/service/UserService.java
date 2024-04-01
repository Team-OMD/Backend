package com.onmydesk.backend.user.service;

import com.onmydesk.backend.user.UserMapper;
import com.onmydesk.backend.user.domain.Authority;
import com.onmydesk.backend.user.domain.User;
import com.onmydesk.backend.user.dto.UserRequest;
import com.onmydesk.backend.user.dto.UserResponse;
import com.onmydesk.backend.user.exception.DuplicateMemberException;
import com.onmydesk.backend.user.exception.NotFoundMemberException;
import com.onmydesk.backend.user.repository.UserRepository;
import com.onmydesk.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Transactional
    public String signup(UserRequest request) { // 회원가입
        if (userRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        User user = userMapper.toEntity(request);
        userRepository.save(user);
        return "회원가입이 완료되었습니다.";
    }

//    @Transactional(readOnly = true)
//    public UserRequest getUserWithAuthorities(String email) {
//        return UserRequest.from(userRepository.findOneWithAuthoritiesByEmail(email).orElse(null));
////        // userRepository.findOneWithAuthoritiesByEmail 메소드가 Optional<User>를 반환한다고 가정합니다.
////        User user = userRepository.findOneWithAuthoritiesByEmail(email)
////                .orElseThrow(() -> new NotFoundMemberException("해당 유저가 존재하지 않습니다."));
////
////        // User 객체를 UserRequest DTO로 변환합니다.
////        UserRequest userRequest = UserRequest.builder()
////                .username(user.getUsername())
////                .email(user.getEmail())
////                // 비밀번호는 DTO에 포함시키지 않는 것이 일반적입니다. 보안 상 제외될 수 있음.
////                .nickname(user.getNickname())
////                // 필요한 다른 필드들을 여기에 추가합니다.
////                .build();
////
////        return userRequest;
//    }


    @Transactional
    public String delete() {

        User user = SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new NotFoundMemberException("Member not found"));

        userRepository.delete(user);
        return "정상적으로 탈퇴되었습니다.";
    }

    @Transactional(readOnly = true)
    public UserResponse getMyUserWithAuthorities() {
        User user = getUser();
        return userMapper.toResponse(user);
    }


    public User getUser(){
        User user = SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return user;
    }
}