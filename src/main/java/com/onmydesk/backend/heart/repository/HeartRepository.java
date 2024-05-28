package com.onmydesk.backend.heart.repository;

import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByMemberAndPost(Member member, Post post);

    List<Heart> findAllByMember(Member member);

    @Query("SELECT h.post.id FROM Heart h WHERE h.createdAt > :localDateTime GROUP BY h.post.id ORDER BY COUNT(h.post.id) DESC, h.post.id DESC")
    List<Long> findTop5PostIds(LocalDateTime localDateTime, Pageable pageable);
}
