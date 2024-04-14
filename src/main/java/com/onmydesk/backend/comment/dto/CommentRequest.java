package com.onmydesk.backend.comment.dto;

import com.onmydesk.backend.comment.domain.Comment;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "댓글을 생성할 때 필요한 데이터")
public class CommentRequest {

    @NotBlank
    @Schema(description = "댓글 내용", nullable = false, example = "title")
    private String content;

    public Comment toEntity(Member member, Post post){
        return Comment.builder()
                .member(member)
                .post(post)
                .content(content)
                .build();
    }

}
