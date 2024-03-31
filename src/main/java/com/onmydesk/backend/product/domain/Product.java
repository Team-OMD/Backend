package com.onmydesk.backend.product.domain;

import com.onmydesk.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String img;

    @Column(name = "product_code", length = 20, nullable = false)
    private String productCode;

    @Column(length = 20, nullable = false)
    private String brand;

    @Column(length = 20, nullable = false)
    private String maker;

    @Column(length = 20)
    private String category1;

    @Column(length = 20)
    private String category2;

    @Column(length = 20)
    private String category3;

    @Column(length = 20)
    private String category4;
}
