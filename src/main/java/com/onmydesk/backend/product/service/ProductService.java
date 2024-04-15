package com.onmydesk.backend.product.service;

import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.product.dto.PageRequest;
import com.onmydesk.backend.product.dto.ProductRequest;
import com.onmydesk.backend.product.repository.PageRepository;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PageRepository pageRepository;
    private final PostMapper postMapper;

    // 상품 목록 조회
    public List<Product> getList() {
        return productRepository.findAll();
    }

    // 상품 개별 조회
    public Product getFind(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product saveProduct(ProductRequest productRequest) {
        return productRepository.findByProductCode(productRequest.getProductCode())
                .orElseGet(() -> {
                    Product product = postMapper.toProductEntity(productRequest);
                    product = productRepository.save(product);
                    for (PageRequest pageRequest : productRequest.getPages()) {
                        Page page = postMapper.toPageEntity(pageRequest, product);
                        pageRepository.save(page);
                    }
                    return product;
                });
    }
}
