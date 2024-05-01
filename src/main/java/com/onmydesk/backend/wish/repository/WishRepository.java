package com.onmydesk.backend.wish.repository;

import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.wish.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByMemberAndProduct(Member member, Product product);

    List<Wish> findAllByMember(Member member);
}
