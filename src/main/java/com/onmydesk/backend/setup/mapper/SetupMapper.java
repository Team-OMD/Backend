package com.onmydesk.backend.setup.mapper;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.setup.domain.Setup;
import com.onmydesk.backend.setup.domain.SetupProduct;
import com.onmydesk.backend.setup.dto.SetupRequest;
import com.onmydesk.backend.setup.dto.SetupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupMapper {

    private final ProductMapper productMapper;

    public Setup toSetupEntity(SetupRequest request, Member member) {

        // 상품 가격 합산 로직 추가
        double totalPriceDouble = productMapper.calculateTotalPrice(request.getProducts());
        int postTotalPrice = (int) totalPriceDouble;

        return Setup.builder()
                .member(member)
                .setupName(request.getSetupName())
                .postTotalPrice(postTotalPrice)
                .build();
    }

    public SetupProduct toSetupProductEntity(Setup setup, Product product) {
        return SetupProduct.builder()
                .setup(setup)
                .product(product)
                .build();
    }

    public SetupResponse toResponse(Setup setup) {
        return SetupResponse.builder()
                .id(setup.getId())
                .setupName(setup.getSetupName())
                .postTotalPrice(setup.getPostTotalPrice())
                .createdAt(setup.getCreatedAt())
                .updatedAt(setup.getUpdatedAt())
                .build();
    }
}
