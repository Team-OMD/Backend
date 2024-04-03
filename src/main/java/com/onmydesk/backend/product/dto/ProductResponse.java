package com.onmydesk.backend.product.dto;

import com.onmydesk.backend.page.domain.Page;
import com.onmydesk.backend.product.domain.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductResponse {

    private final Long id;
    private final String productName;
    private final String img;
    private final String brand;
    private final String maker;
    private final List<Page> pages;

    public ProductResponse(Product product){
        this.id = product.getId();
        this.productName = product.getProductName();
        this.img = product.getImg();
        this.brand = product.getBrand();
        this.maker = product.getMaker();
        this.pages = product.getPages();
    }
}
