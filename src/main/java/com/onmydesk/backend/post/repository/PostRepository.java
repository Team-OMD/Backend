package com.onmydesk.backend.post.repository;

import com.onmydesk.backend.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}