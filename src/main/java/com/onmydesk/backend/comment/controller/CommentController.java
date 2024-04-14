package com.onmydesk.backend.comment.controller;

import com.onmydesk.backend.comment.dto.CommentRequest;
import com.onmydesk.backend.comment.dto.CommentResponse;
import com.onmydesk.backend.comment.service.CommentService;
import com.onmydesk.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final ApiResponse apiResponse;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable("postId") Long postId, @RequestBody CommentRequest request) {
        commentService.save(request, postId);
        return apiResponse.success("댓글 생성 성공", HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable("postId") Long postId) {
        List<CommentResponse> commentListResponse = commentService.list(postId);
        return apiResponse.success("댓글 조회 성공", commentListResponse, HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @RequestBody CommentRequest request) {
        CommentResponse commentResponse = commentService.update(postId, commentId, request);
        return apiResponse.success("댓글 수정 성공", commentResponse, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.delete(postId, commentId);
        return apiResponse.success("댓글 삭제 성공", HttpStatus.NO_CONTENT);
    }
}
