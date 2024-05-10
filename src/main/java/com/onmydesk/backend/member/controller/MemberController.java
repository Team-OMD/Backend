package com.onmydesk.backend.member.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.jwt.JwtFilter;
import com.onmydesk.backend.jwt.TokenProvider;
import com.onmydesk.backend.member.dto.*;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.service.PostService;
import com.onmydesk.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 API")
public class MemberController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;
    private final PostService postService;
    private final ProductService productService;
    private final ApiResponse apiResponse;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원이 회원가입을 한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> signIn(@Valid @RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.signup(request);
        return apiResponse.success("회원가입 성공", memberResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원이 로그인을 한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> authorize(@Valid @RequestBody MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponseDto token = tokenProvider.createToken(authentication);
        String jwt = token.getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        redisTemplate.opsForValue().set(authentication.getName(), token.getRefreshToken(),
                token.getRefreshTokenValidationTime(), TimeUnit.MICROSECONDS);


        return apiResponse.success("로그인 성공", token, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "회원이 로그아웃을 한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> logout(@RequestBody TokenRequestDto request) {
        memberService.logout(request);
        return apiResponse.success("로그아웃 완료", HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "회원정보 조회", description = "회원정보를 단건으로 조회한다")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getMyUserInfo() {
        MemberResponse memberResponse = memberService.getMyMemberWithAuthorities();
        return apiResponse.success("개인 정보 조회 성공", memberResponse, HttpStatus.OK);
    }

    @PutMapping("/user")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "회원 정보 수정", description = "회원정보를 수정한다")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> update(@Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse memberResponse = memberService.update(request);
        return apiResponse.success("회원 정보 수정 성공", memberResponse, HttpStatus.OK);
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "회원 탈퇴", description = "회원탈퇴(논리삭제)를 한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> delete() {
        memberService.delete();
        return apiResponse.success("회원 탈퇴 성공", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/posts/hearts")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "좋아요한 게시물 조회", description = "좋아요한 게시물 목록을 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getMyHeartPost() {
        return apiResponse.success("좋아요한 게시물 조회 성공", postService.getHeartPost(), HttpStatus.OK);
    }

    @GetMapping("/user/products/wishes")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "찜한 상품 조회", description = "찜한 상품을 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getMyWishProduct() {
        return apiResponse.success("찜한 상품 조회 성공", productService.getWishProduct(), HttpStatus.OK);
    }
}