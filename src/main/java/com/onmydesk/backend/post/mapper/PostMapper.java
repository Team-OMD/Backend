package com.onmydesk.backend.post.mapper;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.domain.PostProduct;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.PageRequest;
import com.onmydesk.backend.product.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public Post toPostEntity(PostRequest request, Member member) {

        // 상품 가격 합산 로직 추가
        double totalPriceDouble = calculateTotalPrice(request.getProducts());
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

    public Product toProductEntity(ProductRequest request) {
        return Product.builder()
                .productName(request.getProductName())
                .img(request.getImg())
                .productCode(request.getProductCode())
                .brand(request.getBrand())
                .maker(request.getMaker())
                .category1(request.getCategory1())
                .category2(request.getCategory2())
                .category3(request.getCategory3())
                .category4(request.getCategory4())
                .build();
    }

    public Page toPageEntity(PageRequest request, Product product) {
        return Page.builder()
                .product(product)
                .price(request.getPrice())
                .link(request.getLink())
                .storeName(request.getStoreName())
                .build();
    }

    public PostProduct toPostProductEntity(Post post, Product product) {
        return PostProduct.builder()
                .product(product)
                .post(post)
                .build();
    }

    private double calculateTotalPrice(List<ProductRequest> products) {
        if (products == null || products.isEmpty()) {
            return 0;
        }
        return products.stream()
                .mapToDouble(ProductRequest::getLprice)
                .sum();
    }

    public PostResponse toResponse(Post post) {

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
                .build();
    }
}
