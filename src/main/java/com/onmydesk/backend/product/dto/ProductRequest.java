package com.onmydesk.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductRequest {

    @NotBlank
    @Schema(description = "상품 코드", nullable = false, example = "Apple <b>아이패드</b> 에어 5세대 M1 WIFI 64G 스페이스 그레이 (MM9C3KH/A)")
    private String productName;

    @NotBlank
    @Schema(description = "상품 이미지 URL", nullable = false, example = "https://shopping-phinf.pstatic.net/main_3153084/31530843620.20220705164247.jpg")
    private String img;

    @NotBlank
    @Schema(description = "상품 고유번호", nullable = false, example = "31530843620")
    private String productCode;

    @NotBlank
    @Schema(description = "최저가", nullable = false, example = "828490")
    private int lprice;

    @NotBlank
    @Schema(description = "브랜드", nullable = false, example = "Apple")
    private String brand;

    @NotBlank
    @Schema(description = "제조사", nullable = false, example = "Apple")
    private String maker;

    @NotBlank
    @Schema(description = "카테고리1", nullable = false, example = "디지털/가전")
    private String category1;

    @NotBlank
    @Schema(description = "카테고리2", nullable = false, example = "태블릿PC")
    private String category2;

    @NotBlank
    @Schema(description = "카테고리3", nullable = false, example = "")
    private String category3;

    @NotBlank
    @Schema(description = "카테고리4", nullable = false, example = "")
    private String category4;
}
