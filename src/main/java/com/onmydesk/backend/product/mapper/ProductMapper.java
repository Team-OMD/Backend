package com.onmydesk.backend.product.mapper;

import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public Product toProductEntity(ProductRequest request) {
        return Product.builder()
                .productName(request.getProductName())
                .img(request.getImg())
                .productCode(request.getProductCode())
                .brand(request.getBrand())
                .maker(request.getMaker())
                .category1(request.getCategory1())
                .category2(request.getCategory2())
                .category3(request.getCategory3())
                .category4(request.getCategory4())
                .build();
    }

    public double calculateTotalPrice(List<ProductRequest> products) {
        if (products == null || products.isEmpty()) {
            return 0;
        }
        return products.stream()
                .mapToDouble(ProductRequest::getLprice)
                .sum();
    }

    public ProductInfoResponse toInfoResponse(Product product) {
        return ProductInfoResponse.builder()
                .productName(product.getProductName())
                .img(product.getImg())
                .lprice(product.getLprice())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .img(product.getImg())
                .productCode(product.getProductCode())
                .lprice(product.getLprice())
                .brand(product.getBrand())
                .maker(product.getMaker())
                .category1(product.getCategory1())
                .category2(product.getCategory2())
                .category3(product.getCategory3())
                .category4(product.getCategory4())
                .build();
    }
}
