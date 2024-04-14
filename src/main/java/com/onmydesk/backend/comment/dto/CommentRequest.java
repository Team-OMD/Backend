package com.onmydesk.backend.comment.dto;

import com.onmydesk.backend.comment.domain.Comment;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentRequest {

    private String content;

    public Comment toEntity(Member member, Post post){
        return Comment.builder()
                .member(member)
                .post(post)
                .content(content)
                .build();
    }

}
