package com.onmydesk.backend.post.controller;

import com.onmydesk.backend.post.dto.PostAndProductResponse;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.service.PostService;
import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.s3.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 API")
public class PostController {

    private final PostService postService;
    private final ApiResponse apiResponse;


    // 게시글 생성
    @PostMapping("/posts")
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성"))
    public ResponseEntity<?> createPost(@RequestBody PostRequest request) {
        postService.savePost(request);
        return apiResponse.success("게시글 생성 성공", HttpStatus.CREATED);
    }

    // 게시글 목록 조회
    @GetMapping("/posts")
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회한다. criteria: 1.생성일 2.좋아요 3.조회수")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getList(
            @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "1") Integer criteria
    ){
        List<PostResponse> postResponses = postService.list(page, limit, criteria);
        return apiResponse.success("게시글 목록 조회 성공", postResponses, HttpStatus.OK);
    }


    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 정보를 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        PostAndProductResponse postAndProductResponse = postService.find(postId);
        return apiResponse.success("게시글 상세 조회 성공", postAndProductResponse, HttpStatus.OK);
    }

    // 게시글 업데이트
    @PutMapping("/posts/{postId}")
    @Operation(summary = "게시글 업데이트", description = "게시글 정보를 수정한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                        @RequestBody PostRequest request) {
        PostResponse postResponse = postService.update(postId, request);
        return apiResponse.success("게시글 업데이트 성공", postResponse, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제"))
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
        postService.delete(postId);
        return apiResponse.success("게시글 삭제 성공", HttpStatus.NO_CONTENT);
    }

}
