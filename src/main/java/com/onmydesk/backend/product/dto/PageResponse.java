package com.onmydesk.backend.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse {

    private final Long id;
    private final int price;
    private final String link;
    private final String storeName;

}
