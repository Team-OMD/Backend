package com.onmydesk.backend.post.service;

import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    // 게시글 생성
    @Transactional
    public Post save(PostRequest request) {
        return postRepository.save(request.toEntity());
    }

    // 게시글 목록 조회
    public List<Post> list() {
        return postRepository.findAll();
    }

    // 게시글 단일 조회
    public Post find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        return post;
    }

    @Transactional
    public Post update(Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        post.update(request.getTitle(), request.getContent());
        return post;
    }

    public void delete(Long postId){
        postRepository.deleteById(postId);
    }
}
