package com.onmydesk.backend.member.service;

import com.onmydesk.backend.error.errorcode.MemberErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.dto.*;
import com.onmydesk.backend.member.mapper.MemberMapper;
import com.onmydesk.backend.member.repository.MemberRepository;
import com.onmydesk.backend.security.config.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public MemberResponse signup(MemberRequest request) {
        if (memberRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new RestApiException(MemberErrorCode.DUPLICATE_MEMBER);
        }

        Member member = memberMapper.toEntity(request);

        return memberMapper.toResponse(memberRepository.save(member));
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