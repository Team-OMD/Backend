package com.onmydesk.backend.wish.repository;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.wish.domain.Wish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByMemberAndProduct(Member member, Product product);

    List<Wish> findAllByMember(Member member);

    @Query("SELECT w.product.id FROM Wish w " +
            "WHERE w.createdAt > :localDateTime " +
            "GROUP BY w.product.id " +
            "ORDER BY COUNT(w.product.id) DESC, w.product.id DESC")
    List<Long> findTopProductIds(LocalDateTime localDateTime, Pageable pageable);
}
