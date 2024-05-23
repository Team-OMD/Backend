package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.product.dto.ProductInfoResponse;
import com.onmydesk.backend.product.dto.ProductResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostAndProductResponse {

    private final PostResponse post;
    private final List<ProductResponse> products;


    public PostAndProductResponse(PostResponse postResponse, List<ProductResponse> productResponses) {
        this.post = postResponse;
        this.products = productResponses;
    }
}
