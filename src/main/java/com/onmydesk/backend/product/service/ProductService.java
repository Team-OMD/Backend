package com.onmydesk.backend.product.service;

import com.onmydesk.backend.product.dto.*;
import com.onmydesk.backend.product.mapper.PageMapper;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.repository.PageRepository;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PageRepository pageRepository;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;

    // 상품 목록 조회
    public List<ProductResponse> getList(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        org.springframework.data.domain.Page<Product> products = productRepository.findAll(pageable);

        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 상품 개별 조회
    public ProductAndPageResponse getFind(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        List<Page> pages = pageRepository.findByProductId(productId);

        List<PageResponse> pageResponses = pages.stream()
                .map(pageMapper::toResponse)
                .collect(Collectors.toList());

        ProductResponse productResponse = productMapper.toResponse(product);


        return ProductAndPageResponse.builder()
                .product(productResponse)
                .pages(pageResponses)
                .build();
    }
    // 상품 저장
    @Transactional
    public Product saveProduct(ProductRequest productRequest) {
        return productRepository.findByProductCode(productRequest.getProductCode())
                .orElseGet(() -> {
                    Product product = productMapper.toProductEntity(productRequest);
                    product = productRepository.save(product);
                    for (com.onmydesk.backend.product.dto.PageRequest pageRequest : productRequest.getPages()) {
                        Page page = pageMapper.toPageEntity(pageRequest, product);
                        pageRepository.save(page);
                    }
                    return product;
                });
    }
}
