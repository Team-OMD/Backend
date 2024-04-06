package com.onmydesk.backend.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onmydesk.backend.product.domain.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductListResponse {
    private final Long id;
    private final String productName;
    private final String img;
    private final String category1;
    private final String category2;
    private final String category3;
    private final String category4;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime updatedAt;

    public ProductListResponse(Product product){
        this.id = product.getId();
        this.productName = product.getProductName();
        this.img = product.getImg();
        this.category1 = product.getCategory1();
        this.category2 = product.getCategory2();
        this.category3 = product.getCategory3();
        this.category4 = product.getCategory4();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}
