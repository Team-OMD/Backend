package com.onmydesk.backend.product.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onmydesk.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE page SET is_deleted = true WHERE page_id=?")
public class Page extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(nullable = false)
    private int price;

    @Column(length = 1000, nullable = false)
    private String link;

    @Column(name = "store_name", length = 20, nullable = false)
    private String storeName;
}
