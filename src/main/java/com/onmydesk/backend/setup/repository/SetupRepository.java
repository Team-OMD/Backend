package com.onmydesk.backend.setup.repository;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.setup.domain.Setup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetupRepository extends JpaRepository<Setup, Long> {
    Page<Setup> findByMember(Member member, Pageable pageable);
}
