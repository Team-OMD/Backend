package com.onmydesk.backend.product.service;

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
}
