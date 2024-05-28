package com.onmydesk.backend.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onmydesk.backend.config.redis.RedisUtil;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.post.dto.PostPreviewResponse;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularPost {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RedisUtil redisUtil;
    private final HeartRepository heartRepository;

    private static final String POPULAR_POSTS_KEY_PREFIX = "popular_posts::";

    public List<PostPreviewResponse> getList() {
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = POPULAR_POSTS_KEY_PREFIX + day;
        String cachedData = redisUtil.getValues(redisKey);

        if (redisUtil.checkExistsValue(cachedData)) {
            // Redis에 데이터가 있는 경우
            System.out.println("Cached Data: " + cachedData);
            return deserializePostPreviews(cachedData);
        } else {
            // Redis에 데이터가 없는 경우 인기글 목록 업데이트 후 가져오기
            updateList();
            cachedData = redisUtil.getValues(redisKey);
            System.out.println("Updated Data: " + cachedData);
            return deserializePostPreviews(cachedData);
        }
    }


    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateList() {
        // 현재 시간 가져오기
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // 일주일 동안 좋아요를 많이 받은 게시글 id 5개 가져오기
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> popularPostIds = heartRepository.findTop5PostIds(oneWeekAgo, pageable);

        // 인기 게시글들을 PostPreviewResponse로 변환
        List<PostPreviewResponse> popularPosts = popularPostIds.stream()
                .map(postId -> postMapper.toPreviewResponse(postRepository.findById(postId).orElse(null), false))
                .collect(Collectors.toList());

        // PostPreviewResponse 리스트를 JSON 형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String popularPostsJson;
        try {
            popularPostsJson = objectMapper.writeValueAsString(popularPosts);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        // popular_posts::yyyyMMdd 형태의 Key에 Value 저장
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = POPULAR_POSTS_KEY_PREFIX + day;
        redisUtil.setValues(redisKey, popularPostsJson, Duration.ofHours(24));
    }

    // JSON 형태의 값을 역직렬화하는 메서드
    public List<PostPreviewResponse> deserializePostPreviews(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(json, new TypeReference<List<PostPreviewResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("JSON Data: " + json); // 디버그 출력
            return new ArrayList<>();
        }
    }
}
