package com.onmydesk.backend.comment.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE comment SET is_deleted = true WHERE comment_id=?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long id;

    // 멤버 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",updatable = false)
    private Member member;

    // 게시글 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", updatable = false)
    private Post post;

    // 댓글 내용
    @Column(length = 200)
    private String content;

    public void update(String content){
        this.content = content;
    }



}
