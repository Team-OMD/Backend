package com.onmydesk.backend.post.repository;

import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    // 좋아요 개수 증가
    @Modifying
    @Transactional
    @Query("update Post p set p.heartCount = p.heartCount + 1 where p = :selectedPost")
    void addHeartCount(Post selectedPost);

    // 좋아요 개수 감소
    @Modifying
    @Transactional
    @Query("update Post p set p.heartCount = p.heartCount - 1 where p = :selectedPost")
    void subHeartCount(Post selectedPost);

    Post findByHeart(Heart heart);

    Page<Post> findAll(Pageable pageable);
}
