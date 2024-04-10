package com.onmydesk.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageRequest {

    @NotBlank
    @Schema(description = "상품 가격", nullable = false, example = "829690")
    private int price;

    @NotBlank
    @Schema(description = "상품 구매 링크", nullable = false, example = "https://search.shopping.naver.com/gate.nhn?id=31530843620")
    private String link;

    @NotBlank
    @Schema(description = "쇼핑몰", nullable = false, example = "네이버")
    private String storeName;
}
