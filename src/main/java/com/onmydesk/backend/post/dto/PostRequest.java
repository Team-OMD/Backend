package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostRequest {
    private String title;
    private String content;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .heartCount(0)
                .viewCount(0)
                .build();
    }

}
