package com.onmydesk.backend.product.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.ProductListResponse;
import com.onmydesk.backend.product.dto.ProductResponse;
import com.onmydesk.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final ApiResponse apiResponse;

    // 상품 목록 조회
    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        List<Product> products = productService.getList();
        List<ProductListResponse> productListResponses = products.stream()
                .map(ProductListResponse::new)
                .collect(Collectors.toList());
        return apiResponse.success("상품 목록 조회 성공", productListResponses,HttpStatus.OK);
    }

    // 상품 개별 조회
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> findProduct(@PathVariable Long productId) {
        Product product = productService.getFind(productId);
        return apiResponse.success("개별 상품 조회 성공", new ProductResponse(product), HttpStatus.OK);
    }
}
