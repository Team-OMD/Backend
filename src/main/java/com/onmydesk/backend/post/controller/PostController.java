package com.onmydesk.backend.post.controller;

import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.NewPostResponse;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.service.PostService;
import com.onmydesk.backend.global.ApiResponse; // ApiResponse 클래스 임포트 필요
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
    private final ApiResponse apiResponse;

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostRequest request) {
        Post post = postService.save(request);
        return apiResponse.success("게시글 생성 성공", new NewPostResponse(post), HttpStatus.CREATED);
    }

    // 게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<?> getList(){
        List<Post> posts = postService.list();
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
        return apiResponse.success("게시글 목록 조회 성공", postResponses, HttpStatus.OK);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        Post post = postService.find(postId);
        return apiResponse.success("게시글 상세 조회 성공", new PostResponse(post), HttpStatus.OK);
    }

    // 게시글 업데이트
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                        @RequestBody PostRequest request) {
        Post post = postService.update(postId, request);
        return apiResponse.success("게시글 업데이트 성공", new PostResponse(post), HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deleteArticle(@PathVariable("postId") Long postId) {
        postService.delete(postId);
        return apiResponse.success("게시글 삭제 성공", HttpStatus.OK);
    }

}
