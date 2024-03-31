package com.onmydesk.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onmydesk.backend.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NewPostResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final int totalPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    public NewPostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.totalPrice = post.getTotalPrice();
        this.createdAt = post.getCreatedAt();
    }
}
