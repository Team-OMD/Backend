package com.onmydesk.backend.post.domain;

import com.onmydesk.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 멤버 정보, 이미지는 추후에 추가

    // 제목
    @Column(name = "title", length = 20, nullable = false)
    private String title;

    // 내용
    @Column(name = "content",columnDefinition="TEXT", nullable = false)
    private String content;

    // 좋아요 수
    @Column(name = "heart_count", nullable = false)
    private int heartCount;

    // 조회수
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    // 추후에 nullable=false 추가
    @Column(name = "post_total_price" )
    private int totalPrice;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}