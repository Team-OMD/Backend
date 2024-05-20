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

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    // 좋아요 개수 증가
    @Modifying
    @Transactional
    @Query("update Post p set p.heartCount = p.heartCount + 1 where p = :post")
    void addHeartCount(Post post);

    // 좋아요 개수 감소
    @Modifying
    @Transactional
    @Query("update Post p set p.heartCount = p.heartCount - 1 where p = :post")
    void subHeartCount(Post post);

    @Modifying
    @Transactional
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p = :post")
    void addViewCount(Post post);

    @Query("select p from Post p " +
            "join fetch p.member " +
            "left join fetch p.images " +
            "where p.id = :postId")
    Optional<Post> findById(Long postId);

    @Query("select distinct p from Post p " +
            "join fetch p.member " +
            "left join fetch p.images")
    Page<Post> findAll(Pageable pageable);
}
