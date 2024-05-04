package com.onmydesk.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductInfoResponse {

    private final Long id;
    private final String productName;
    private final String img;
    private final int lprice;
}
