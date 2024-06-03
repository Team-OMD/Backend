package com.onmydesk.backend.product.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.product.dto.ProductAndPageResponse;
import com.onmydesk.backend.product.dto.ProductResponse;
import com.onmydesk.backend.product.service.PopularProduct;
import com.onmydesk.backend.product.service.ProductSearchAndCrawlingService;
import com.onmydesk.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "상품", description = "상품 API")
public class ProductController {

    private final ProductService productService;
    private final ProductSearchAndCrawlingService productSearchAndCrawlingService;
    private final ApiResponse apiResponse;
    private final PopularProduct popularProduct;

    // 상품 목록 조회
    @GetMapping("/products")
    @Operation(summary = "상품 목록 조회", description = "상품의 전체 목록을 조회한다. criteria: 1.게시글에 등록된 횟수 2.좋아요 3.조회수")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "1") Integer criteria) {
        List<ProductResponse> productResponses = productService.getList(page, limit, criteria);
        return apiResponse.success("상품 목록 조회 성공", productResponses,HttpStatus.OK);
    }

    // 상품 개별 조회
    @GetMapping("/products/{productId}")
    @Operation(summary = "상품 개별 조회", description = "상품의 상세 정보를 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> findProduct(@PathVariable Long productId) {
        ProductAndPageResponse productAndPageResponse = productService.getFind(productId);
        return apiResponse.success("개별 상품 조회 성공", productAndPageResponse, HttpStatus.OK);
    }

    // 상품 검색
    @GetMapping("/products/search")
    @Operation(summary = "상품 검색", description = "상품을 검색한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public String searchProduct(@RequestParam(value = "query") String query,
                                @RequestParam(value = "display", defaultValue = "10") int display,
                                @RequestParam(value = "start", defaultValue = "1") int start) {
        return productSearchAndCrawlingService.searchProduct(query, display, start);
    }

    // 인기 상품 조회
    @GetMapping("/products/popular")
    @Operation(summary = "인기 상품 목록 조회", description = "인기 상품 목록을 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getPopularPosts( ){
        List<ProductResponse> postResponses = popularProduct.getList();
        return apiResponse.success("인기 상품 조회 성공", postResponses, HttpStatus.OK);
    }
}
