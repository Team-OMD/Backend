package com.onmydesk.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductAndPageResponse {

    private ProductResponse product;
    private List<PageResponse> pages;

    public ProductAndPageResponse(ProductResponse productResponse, List<PageResponse> pageResponses) {
        this.product = productResponse;
        this.pages = pageResponses;
    }
}
