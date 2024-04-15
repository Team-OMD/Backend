package com.onmydesk.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private final Long id;
    private final String productName;
    private final String img;
    private final String productCode;
    private final int lprice;
    private final String brand;
    private final String maker;
    private final String category1;
    private final String category2;
    private final String category3;
    private final String category4;
}
