package com.onmydesk.backend.product.controller;

import com.onmydesk.backend.product.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/products/search")
    public String searchProduct(@RequestParam(value = "query") String query,
                                @RequestParam(value = "display", defaultValue = "10") int display) {
        return productSearchService.searchProduct(query, display);
    }
}
