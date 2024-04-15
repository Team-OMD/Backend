package com.onmydesk.backend.setup.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.product.domain.Product;
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
@SQLDelete(sql = "UPDATE setup_product SET is_deleted = true WHERE setup_product_id=?")
public class SetupProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setup_product_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="setup_id", updatable = false)
    private Setup setup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", updatable = false)
    private Product product;

}
