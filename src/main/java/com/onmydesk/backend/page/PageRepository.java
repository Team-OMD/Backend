package com.onmydesk.backend.page;

import com.onmydesk.backend.page.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
}
