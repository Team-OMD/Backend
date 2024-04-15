package com.onmydesk.backend.post.controller;

import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.service.PostService;
import com.onmydesk.backend.global.ApiResponse; // ApiResponse 클래스 임포트 필요
import com.onmydesk.backend.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ApiResponse apiResponse;
    private final S3Uploader s3Uploader;

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestPart("request") PostRequest request,
                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        String fileName = null;
        if (file != null && !file.isEmpty()) {
            try {
                fileName = s3Uploader.upload(file, "images");
                System.out.println("Uploaded file name: " + fileName);
            } catch (Exception e) {
                return apiResponse.error("파일 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Post post = postService.save(request, fileName);  // DB 저장 로직은 파일명을 포함하도록 수정 필요
        return apiResponse.success("게시글 생성 성공", HttpStatus.CREATED);
    }

    // 게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<?> getList(){
        List<PostResponse> postResponses = postService.list();
        return apiResponse.success("게시글 목록 조회 성공", postResponses, HttpStatus.OK);
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        PostResponse postResponse = postService.find(postId);
        return apiResponse.success("게시글 상세 조회 성공", postResponse, HttpStatus.OK);
    }

    // 게시글 업데이트
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                        @RequestBody PostRequest request) {
        PostResponse postResponse = postService.update(postId, request);
        return apiResponse.success("게시글 업데이트 성공", postResponse, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
        postService.delete(postId);
        return apiResponse.success("게시글 삭제 성공", HttpStatus.OK);
    }

}
