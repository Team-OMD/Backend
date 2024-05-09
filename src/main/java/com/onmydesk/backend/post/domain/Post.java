package com.onmydesk.backend.post.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.s3.domain.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE post_id=?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false)
    private Long id;

    // 멤버 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false)
    private Member member;

    // 좋아요 정보
    @OneToMany(mappedBy = "post")
    private List<Heart> heart;

    // 이미지는 추후에 추가

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
    @Column(name = "post_total_price")
    private int totalPrice;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostProduct> postProducts;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images = new HashSet<>();  // 초기화 추가

    @ManyToOne
    private Image thumbnailImage;

    public void addImage(Image image) {
        this.images.add(image);
        image.setPost(this);  // 이미지 엔티티에도 포스트를 설정해야 할 수 있습니다.
    }

    public void setThumbnailImage(Image thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public void update(String title, String content, int totalPrice) {
        this.title = title;
        this.content = content;
        this.totalPrice = totalPrice;
    }
}