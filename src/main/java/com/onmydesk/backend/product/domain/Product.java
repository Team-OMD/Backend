package com.onmydesk.backend.product.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.wish.domain.Wish;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE product_id=?")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(length = 100, nullable = false)
    private String img;

    @Column(name = "product_code", length = 20, nullable = false)
    private String productCode;

    @Column(nullable = false)
    private int lprice;

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

    @Column(name = "post_count", nullable = false)
    private int postCount;

    @Column(name = "wish_count", nullable = false)
    private int wishCount;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Page> pages = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Wish> wish;
}
