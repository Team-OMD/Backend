package com.onmydesk.backend.product.repository;

import com.onmydesk.backend.product.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByProductId(Long productId);
}
