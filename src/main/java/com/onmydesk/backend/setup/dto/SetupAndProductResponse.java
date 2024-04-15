package com.onmydesk.backend.setup.dto;

import com.onmydesk.backend.product.dto.ProductInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SetupAndProductResponse {

    private SetupResponse setup;
    private List<ProductInfoResponse> products;

    public SetupAndProductResponse(SetupResponse setupResponse, List<ProductInfoResponse> productInfoResponses) {
        this.setup = setupResponse;
        this.products = productInfoResponses;
    }
}
