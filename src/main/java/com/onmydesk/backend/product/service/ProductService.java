package com.onmydesk.backend.product.service;

import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.product.ProductRepository;
import com.onmydesk.backend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<Product> getList() {
        return productRepository.findAll();
    }

    // 상품 개별 조회
    public Product getFind(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }
}
