package com.onmydesk.backend.product.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.ProductListResponse;
import com.onmydesk.backend.product.dto.ProductResponse;
import com.onmydesk.backend.product.service.ProductSearchService;
import com.onmydesk.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "상품", description = "상품 API")
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;
    private final ApiResponse apiResponse;

    // 상품 목록 조회
    @GetMapping("/products")
    @Operation(summary = "상품 목록 조회", description = "상품의 전체 목록을 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getProducts() {
        List<Product> products = productService.getList();
        List<ProductListResponse> productListResponses = products.stream()
                .map(ProductListResponse::new)
                .collect(Collectors.toList());
        return apiResponse.success("상품 목록 조회 성공", productListResponses,HttpStatus.OK);
    }

    // 상품 개별 조회
    @GetMapping("/products/{productId}")
    @Operation(summary = "상품 개별 조회", description = "상품의 상세 정보를 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> findProduct(@PathVariable Long productId) {
        Product product = productService.getFind(productId);
        return apiResponse.success("개별 상품 조회 성공", new ProductResponse(product), HttpStatus.OK);
    }

    // 상품 검색
    @GetMapping("/products/search")
    @Operation(summary = "상품 검색", description = "상품을 검색한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public String searchProduct(@RequestParam(value = "query") String query,
                                @RequestParam(value = "display", defaultValue = "10") int display) {
        return productSearchService.searchProduct(query, display);
    }
}
