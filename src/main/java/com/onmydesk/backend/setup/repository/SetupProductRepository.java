package com.onmydesk.backend.setup.repository;

import com.onmydesk.backend.setup.domain.SetupProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetupProductRepository extends JpaRepository<SetupProduct, Long> {

    List<SetupProduct> findBySetupId(Long setupId);

    Optional<SetupProduct> findBySetupIdAndId(Long setupId, Long setupProductId);

}
