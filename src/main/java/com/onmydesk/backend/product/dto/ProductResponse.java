package com.onmydesk.backend.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String productName;
    private String img;
    private String productCode;
    private int lprice;
    private String brand;
    private String maker;
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    private int postCount;
    private int wishCount;
    private int viewCount;

    @JsonProperty("wished")
    private boolean isWished;
}
