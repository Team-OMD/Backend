package com.onmydesk.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String nickname;
    private final int heartCount;
    private final int viewCount;
    private final int totalPrice;
    private final boolean isLiked;
    @Singular("image")
    private final List<ImageInfo> imageUrls; // 게시글에 포함된 이미지 URL 리스트

    private final String thumbnailUrl; // 썸네일 이미지의 URL

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime updatedAt;

}
