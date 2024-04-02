package com.onmydesk.backend.member.mapper;

import com.onmydesk.backend.member.domain.Authority;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.dto.MemberListResponse;
import com.onmydesk.backend.member.dto.MemberRequest;
import com.onmydesk.backend.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public Member toEntity(MemberRequest request) {

        Authority authority = Authority.builder().
                authorityName("ROLE_USER")
                .build();

        Member member = Member.builder()
                .nickname(request.getNickname())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .activated(true)
                .authorities(Collections.singleton(authority))
                .build();

        return member;
    }

    public MemberResponse toResponse(Member member) {

        if (member == null) return null;

        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .build();
    }

    public MemberListResponse toListResponse(List<Member> memberList) {
        List<MemberResponse> memberResponseList =
                memberList.stream().map(this::toResponse).collect(Collectors.toList());
        return MemberListResponse.builder()
                .memberList(memberResponseList)
                .build();
    }
}
