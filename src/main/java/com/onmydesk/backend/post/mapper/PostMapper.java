package com.onmydesk.backend.post.mapper;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public Post toEntity(PostRequest request, Member member) {
        return Post.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .heartCount(0)
                .viewCount(0)
                .build();
    }

    public PostResponse toResponse(Post post) {

        // 회원 정보
        Member member = post.getMember();
        String nickname = (member != null) ? member.getNickname() : null;

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(member.getNickname())
                .heartCount(post.getHeartCount())
                .viewCount(post.getViewCount())
                .totalPrice(post.getTotalPrice())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

}
