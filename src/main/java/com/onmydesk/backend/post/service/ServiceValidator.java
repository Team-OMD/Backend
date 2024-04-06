package com.onmydesk.backend.post.service;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.exception.PostNotFoundException;
import com.onmydesk.backend.post.exception.PostOwnershipException;
import com.onmydesk.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

    @RequiredArgsConstructor
    @Component
    public class ServiceValidator {
        private final PostRepository postRepository;

        public Post validatePostOwnership(Long postId, Member member) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException(postId));
            if (!post.getMember().equals(member)) {
                throw new PostOwnershipException(postId);
            }
            return post;
        }
    }
