package com.onmydesk.backend.product.mapper;

import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.PageRequest;
import com.onmydesk.backend.product.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageMapper {

    public Page toPageEntity(PageRequest request, Product product) {
        return Page.builder()
                .product(product)
                .price(request.getPrice())
                .link(request.getLink())
                .storeName(request.getStoreName())
                .build();
    }

    public PageResponse toResponse(Page page) {
        return PageResponse.builder()
                .id(page.getId())
                .price(page.getPrice())
                .link(page.getLink())
                .storeName(page.getStoreName())
                .build();
    }
}
