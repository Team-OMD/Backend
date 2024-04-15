package com.onmydesk.backend.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onmydesk.backend.comment.domain.Comment;
import com.onmydesk.backend.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private final Long postId;
    private final Long commentId;
    private final String nickname;
    private final String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime updatedAt;

    public static CommentResponse toResponse(Comment comment){

        Member member = comment.getMember();

        return CommentResponse.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .nickname(member.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
