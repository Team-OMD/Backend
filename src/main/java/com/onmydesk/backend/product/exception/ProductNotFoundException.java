package com.onmydesk.backend.product.exception;

public class ProductNotFoundException extends IllegalArgumentException {

    public ProductNotFoundException(Long productId) {
        super("해당 상품이 없습니다.(상품 ID: " + productId +")");
    }
}

