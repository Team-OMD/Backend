package com.onmydesk.backend.member.service;

import com.onmydesk.backend.error.errorcode.MemberErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.jwt.TokenProvider;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.dto.*;
import com.onmydesk.backend.member.mapper.MemberMapper;
import com.onmydesk.backend.member.repository.MemberRepository;
import com.onmydesk.backend.config.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;

    @Transactional
    public MemberResponse signup(MemberRequest request) {
        if (memberRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new RestApiException(MemberErrorCode.DUPLICATE_MEMBER);
        }

        // 닉네임으로 기존 회원 검사
        if (memberRepository.findOneByNickname(request.getNickname()).orElse(null) != null) {
            throw new RestApiException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = memberMapper.toEntity(request);

        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Transactional
    public void logout(TokenRequestDto tokenRequestDto) {
        if (!tokenProvider.validateToken(tokenRequestDto.getAccessToken())) {
            throw new RestApiException(MemberErrorCode.INVALID_ACCESS_TOKEN);
        }

        if (!tokenProvider.validateRefreshToken(tokenRequestDto.getRefreshToken())) {
            throw new RestApiException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        if (redisTemplate.opsForValue().get(authentication.getName()) != null) {
            redisTemplate.delete(authentication.getName());
        }


        Long expiration = tokenProvider.getExpiration(tokenRequestDto.getAccessToken());
        redisTemplate.opsForValue().set(tokenRequestDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }


    @Transactional(readOnly = true)
    public MemberResponse getMyMemberWithAuthorities() {
        return memberMapper.toResponse(
                SecurityUtil.getCurrentUsername()
                        .flatMap(memberRepository::findOneWithAuthoritiesByEmail)
                        .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND))
        );
    }

    @Transactional
    public MemberResponse update(MemberUpdateRequest request) {

        Member member = SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.update(request);

        return memberMapper.toResponse(member);
    }

    @Transactional
    public String delete() {
        Member member = SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
        return "정상적으로 탈퇴되었습니다.";
    }

    public Member getMember() {
        Member member = SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        return member;
    }
}