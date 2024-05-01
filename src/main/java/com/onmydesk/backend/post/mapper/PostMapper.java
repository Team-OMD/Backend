package com.onmydesk.backend.post.mapper;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.domain.PostProduct;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final ProductMapper productMapper;

    public Post toPostEntity(PostRequest request, Member member) {

        // 상품 가격 합산 로직 추가
        double totalPriceDouble = productMapper.calculateTotalPrice(request.getProducts());
        int totalPrice = (int) totalPriceDouble;

        return Post.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .totalPrice(totalPrice)
                .heartCount(0)
                .viewCount(0)
                .build();
    }

    public PostProduct toPostProductEntity(Post post, Product product) {
        return PostProduct.builder()
                .product(product)
                .post(post)
                .build();
    }

    public PostResponse toResponse(Post post, boolean isLiked) {

        // 회원 정보
        Member member = post.getMember();

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(member.getNickname())
                .heartCount(post.getHeartCount())
                .viewCount(post.getViewCount())
                .totalPrice(post.getTotalPrice())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isLiked(isLiked)
                .build();
    }
}
