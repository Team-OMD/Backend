package com.onmydesk.backend.member.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.jwt.JwtFilter;
import com.onmydesk.backend.jwt.TokenProvider;
import com.onmydesk.backend.member.dto.*;
import com.onmydesk.backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;
    private final ApiResponse apiResponse; // ApiResponse 주입

    @PostMapping("/signup")
    public ResponseEntity<?> signIn(@Valid @RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.signup(request);
        return apiResponse.success("회원가입 성공", memberResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorize(@Valid @RequestBody MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        TokenDto tokenDto = new TokenDto(jwt);
        return apiResponse.success("로그인 성공", tokenDto, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getMyUserInfo() {
        MemberResponse memberResponse = memberService.getMyMemberWithAuthorities();
        return apiResponse.success("개인 정보 조회 성공", memberResponse, HttpStatus.OK);
    }

    @PutMapping("/user")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> update(@Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse memberResponse = memberService.update(request);
        return apiResponse.success("회원 정보 수정 성공", memberResponse, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> delete() {
        memberService.delete();
        return apiResponse.success("회원 탈퇴 성공", HttpStatus.NO_CONTENT);
    }
}