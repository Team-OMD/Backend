package com.onmydesk.backend.post.service;

import com.onmydesk.backend.config.redis.RedisUtil;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final PostRepository postRepository;
    private final RedisUtil redisUtil;

    public int updateViewCount(Long postId, String clientIp) {

        String redisKey = "post::viewCount::" + postId; // 조회수 키
        String redisUserKey = "post::users::" + postId; // 게시글 ID를 기반으로 한 사용자 집합 키

        // 기존 조회수 값 가져오기
        String values = redisUtil.getValues(redisKey);
        int views = 0;
        if (values != null) {
            try {
                views = Integer.parseInt(values);
            } catch (NumberFormatException e) {
                // 로그 추가
                System.err.println("Invalid view count format in Redis: " + values);
                views = 0;
            }
        }

        // 클라이언트 IP가 해당 게시글 ID 집합에 포함되지 않는다면,
        if (!redisUtil.isMemberOfSet(redisUserKey, clientIp)) {
            redisUtil.addToSet(redisUserKey, clientIp); // 클라이언트 IP를 집합에 추가
            redisUtil.expireValues(redisUserKey, 1, TimeUnit.HOURS); // 집합에 만료 시간 설정
            views += 1; // 조회수 증가
            redisUtil.setValues(redisKey, String.valueOf(views)); // 글 ID로 조회수 저장
        }

        return views;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void syncViewCountsToDatabase() {

        Set<String> keys = redisUtil.getKeysByPattern("post::viewCount::*");

        for (String key : keys) {
            Long postId = Long.parseLong(key.split("::")[2]);
            String viewCountStr = redisUtil.getValues(key);
            int viewCount = 0;
            try {
                viewCount = Integer.parseInt(viewCountStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid view count format in Redis for key " + key + ": " + viewCountStr);
            }

            if (viewCount > 0) {
                Post post = postRepository.findById(postId).orElse(null);
                if (post != null) {
                    postRepository.addViewCount(post, viewCount); // 조회수를 한 번에 추가
                }
                redisUtil.deleteValues(key);
            }
        }
    }
}
