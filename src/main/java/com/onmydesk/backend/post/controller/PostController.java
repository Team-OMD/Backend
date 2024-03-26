package com.onmydesk.backend.post.controller;

import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.NewPostResponse;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<NewPostResponse> createPost(@RequestBody PostRequest request) {
        Post post = postService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NewPostResponse(post));
    }

    // 게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getList(){
        List<Post> posts = postService.list();
        List<PostResponse> postResponses = posts.stream()  // Stream<Post> 생성
                .map(PostResponse::new)  // Stream<PostResponse>로 변환
                .collect(Collectors.toList());  // List<PostResponse>로 변환
        return ResponseEntity.ok(postResponses);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("postId") Long postId) {
        Post post = postService.find(postId);
        return ResponseEntity.ok(new PostResponse(post));
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("postId") Long postId,
                                                   @RequestBody PostRequest request) {
        Post post = postService.update(postId, request);
        return ResponseEntity.ok(new PostResponse(post));
    }


}