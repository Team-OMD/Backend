package com.onmydesk.backend.post.service;

import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.exception.PostNotFoundException;
import com.onmydesk.backend.post.exception.PostUpdateException;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MemberService memberService;
    private final ServiceValidator serviceValidator;
    private final HeartRepository heartRepository;

    // 게시글 생성
    @Transactional
    public Post save(PostRequest request) {
        Member member = memberService.getMember();
        return postRepository.save(postMapper.toEntity(request, member));
    }

    // 게시글 목록 조회
    public List<PostResponse> list(Integer page, Integer limit, Integer criteria) {
        String sortProperty = switch (criteria) {
            case 1 -> "createdAt";
            case 2 -> "heartCount";
            case 3 -> "viewCount";
            default -> throw new IllegalArgumentException("잘못된 정렬 기준입니다.");
        };

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.DESC, sortProperty);

        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 게시글 단일 조회
    public PostResponse find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return postMapper.toResponse(post);
    }

    // 게시글 업데이트
    @Transactional
    public PostResponse update(Long postId, PostRequest request) {
        Member member = memberService.getMember();
        Post post = serviceValidator.validatePostOwnership(postId, member);

        try {
            post.update(request.getTitle(), request.getContent());
        } catch (Exception e) {
            throw new PostUpdateException(postId);
        }
        return postMapper.toResponse(post);
    }

    public void delete(Long postId) {
        Member member = memberService.getMember();
        Post post = serviceValidator.validatePostOwnership(postId, member);
        postRepository.delete(post);
    }

    // 좋아요 누른 게시물 조회
    public List<PostResponse> getHeartPost() {
        Member member = memberService.getMember();
        List<Heart> heart = heartRepository.findAllByMember(member);
        List<Post> posts = heart.stream().map(postRepository::findByHeart).toList();
        return posts.stream().map(postMapper::toResponse).collect(Collectors.toList());
    }
}
