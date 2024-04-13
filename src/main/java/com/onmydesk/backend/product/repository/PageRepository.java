package com.onmydesk.backend.product.repository;

import com.onmydesk.backend.product.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByProductId(Long productId);
}
