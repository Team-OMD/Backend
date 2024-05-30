package com.onmydesk.backend.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onmydesk.backend.config.redis.RedisUtil;
import com.onmydesk.backend.product.dto.ProductResponse;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.wish.repository.WishRepository;
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
public class PopularProduct {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RedisUtil redisUtil;
    private final WishRepository wishRepository;

    private static final String POPULAR_PRODUCTS_KEY_PREFIX = "popular_products::";

    public List<ProductResponse> getList() {
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = POPULAR_PRODUCTS_KEY_PREFIX + day;
        String cachedData = redisUtil.getValues(redisKey);

        if (redisUtil.checkExistsValue(cachedData)) {
            // Redis에 데이터가 있는 경우
            System.out.println("Cached Data: " + cachedData);
            return deserializeProductResponse(cachedData);
        } else {
            // Redis에 데이터가 없는 경우 인기글 목록 업데이트 후 가져오기
            updateList();
            cachedData = redisUtil.getValues(redisKey);
            System.out.println("Updated Data: " + cachedData);
            return deserializeProductResponse(cachedData);
        }
    }


    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateList() {
        // 현재 시간 가져오기
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // 일주일 동안 좋아요를 많이 받은 상품 id 13개 가져오기
        Pageable pageable = PageRequest.of(0, 13);
        List<Long> popularProductIds = wishRepository.findTopProductIds(oneWeekAgo, pageable);

        // 인기 상품들을 ProductResponse로 변환
        List<ProductResponse> popularProducts = popularProductIds.stream()
                .map(productId -> productMapper.toResponse(productRepository.findById(productId).orElse(null), false))
                .collect(Collectors.toList());

        // ProductResponse 리스트를 JSON 형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String popularProductsJson;
        try {
            popularProductsJson = objectMapper.writeValueAsString(popularProducts);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        // popular_products::yyyyMMdd 형태의 Key에 Value 저장
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = POPULAR_PRODUCTS_KEY_PREFIX + day;
        redisUtil.setValues(redisKey, popularProductsJson, Duration.ofHours(24));
    }

    // JSON 형태의 값을 역직렬화하는 메서드
    public List<ProductResponse> deserializeProductResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("JSON Data: " + json); // 디버그 출력
            return new ArrayList<>();
        }
    }
}
