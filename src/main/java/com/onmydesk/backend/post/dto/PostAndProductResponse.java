package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.product.dto.ProductInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostAndProductResponse {

    private PostResponse post;
    private List<ProductInfoResponse> products;

    public PostAndProductResponse(PostResponse postResponse, List<ProductInfoResponse> productInfoResponses) {
        this.post = postResponse;
        this.products = productInfoResponses;
    }
}
