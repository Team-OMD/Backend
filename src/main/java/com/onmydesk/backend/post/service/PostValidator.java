package com.onmydesk.backend.post.service;

import com.onmydesk.backend.error.errorcode.PostErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

    @RequiredArgsConstructor
    @Component
    public class PostValidator {
        private final PostRepository postRepository;

        public Post validatePostOwnership(Long postId, Member member) {
            Post post = validatePostExists(postId);
            if (!post.getMember().equals(member)) {
                throw new RestApiException(PostErrorCode.NO_PERMISSION);
            }
            return post;
        }

        public Post validatePostExists(Long postId) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));
            return post;
        }
    }
