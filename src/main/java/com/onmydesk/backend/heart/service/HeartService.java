package com.onmydesk.backend.heart.service;

import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.heart.exception.HeartAlreadyExistException;
import com.onmydesk.backend.heart.exception.HeartNotFoundException;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.exception.PostNotFoundException;
import com.onmydesk.backend.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    @Transactional
    public String insert(Long postId) throws Exception {
        Member member = memberService.getMember();

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        if (heartRepository.findByMemberAndPost(member, post).isPresent()) {
            throw new HeartAlreadyExistException(postId);
        }

        Heart heart = Heart.builder()
                .post(post)
                .member(member)
                .build();

        heartRepository.save(heart);
        postRepository.addHeartCount(post);

        return "좋아요를 눌렀습니다.";
    }

    @Transactional
    public String delete(Long postId) {
        Member member = memberService.getMember();

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        Heart heart = heartRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new HeartNotFoundException(postId));

        heartRepository.delete(heart);
        postRepository.subHeartCount(post);

        return "좋아요를 취소했습니다.";
    }

}
