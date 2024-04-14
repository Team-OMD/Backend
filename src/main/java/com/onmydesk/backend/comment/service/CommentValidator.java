package com.onmydesk.backend.comment.service;

import com.onmydesk.backend.comment.domain.Comment;
import com.onmydesk.backend.comment.repository.CommentRepository;
import com.onmydesk.backend.error.errorcode.CommentErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;

    public Comment validateCommentOwnerShip(Long commentId, Member member){
        Comment comment = validateCommentExists(commentId);

        if(!comment.getMember().equals(member)){
            throw new RestApiException(CommentErrorCode.NO_PERMISSION);
        }
        return comment;
    }

    public void validateCommentBelongsToPost(Long commentId, Long postId) {
        Comment comment = validateCommentExists(commentId);

        if (!comment.getPost().getId().equals(postId)) {
            throw new RestApiException(CommentErrorCode.COMMENT_NOT_BELONGS_TO_POST);
        }
    }

    public Comment validateCommentExists(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND));
        return comment;
    }

}
