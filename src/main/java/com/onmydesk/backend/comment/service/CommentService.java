package com.onmydesk.backend.comment.service;

import com.onmydesk.backend.comment.domain.Comment;
import com.onmydesk.backend.comment.dto.CommentRequest;
import com.onmydesk.backend.comment.dto.CommentResponse;
import com.onmydesk.backend.comment.repository.CommentRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.service.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;

    // 댓글 생성
    @Transactional
    public Comment save(CommentRequest request, Long postId) {
        Post post = postValidator.validatePostExists(postId);
        Member member = memberService.getMember();
        return commentRepository.save(request.toEntity(member, post));
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> list(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponse::toResponse)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentResponse update(Long postId, Long commentId, CommentRequest request) {
        commentValidator.validateCommentBelongsToPost(commentId, postId);
        Member member = memberService.getMember();
        Comment comment = commentValidator.validateCommentOwnerShip(commentId, member);
        comment.update(request.getContent());
        return CommentResponse.toResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long postId, Long commentId) {
        commentValidator.validateCommentBelongsToPost(commentId, postId);
        Member member = memberService.getMember();
        Comment comment = commentValidator.validateCommentOwnerShip(commentId, member);
        commentRepository.delete(comment);
    }


}
