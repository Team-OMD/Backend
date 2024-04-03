package com.onmydesk.backend.page.domain;

import com.onmydesk.backend.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int price;

    @Column(length = 600, nullable = false)
    private String link;

    @Column(name = "store_name", length = 20, nullable = false)
    private String storeName;
}
