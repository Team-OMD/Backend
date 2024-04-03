package com.onmydesk.backend.post.service;

import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.exception.PostNotFoundException;
import com.onmydesk.backend.post.exception.PostUpdateException;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    // 게시글 생성
    @Transactional
    public Post save(PostRequest request) {
        return postRepository.save(postMapper.toEntity(request));
    }

    // 게시글 목록 조회
    public List<PostResponse> list() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 게시글 단일 조회
    public PostResponse find(Long postId) {
        Post post =  postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return postMapper.toResponse(post);
    }

    // 게시글 업데이트
    @Transactional
    public PostResponse update(Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        try {
            post.update(request.getTitle(), request.getContent());
        } catch (Exception e) {
            throw new PostUpdateException(postId);
        }
        return postMapper.toResponse(post);
    }

    public void delete(Long postId){
        postRepository.deleteById(postId);
    }
}
