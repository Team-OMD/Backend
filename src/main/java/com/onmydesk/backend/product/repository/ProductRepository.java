package com.onmydesk.backend.product.repository;

import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.wish.domain.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 게시글에 둥록된 횟수 증가
    @Modifying
    @Transactional
    @Query("update Product p set p.postCount = p.postCount + 1 where p = :product")
    void addPostCount(Product product);

    // 게시글에 둥록된 횟수 감소
    @Modifying
    @Transactional
    @Query("update Product p set p.postCount = p.postCount - 1 where p = :product")
    void subPostCount(Product product);

    // 찜 개수 증가
    @Modifying
    @Transactional
    @Query("update Product p set p.wishCount = p.wishCount + 1 where p = :product")
    void addWishCount(Product product);

    // 찜 개수 감소
    @Modifying
    @Transactional
    @Query("update Product p set p.wishCount = p.wishCount - 1 where p = :product")
    void subWishCount(Product product);

    // 조회수 증가
    @Modifying
    @Transactional
    @Query("update Product p set p.viewCount = p.viewCount + 1 where p = :product")
    void addViewCount(Product product);

    Product findByWish(Wish wish);

    Optional<Product> findByProductCode(String productCode);

    Page<Product> findAll(Pageable pageable);

}
