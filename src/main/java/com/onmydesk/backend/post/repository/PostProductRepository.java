package com.onmydesk.backend.post.repository;

import com.onmydesk.backend.post.domain.PostProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostProductRepository extends JpaRepository<PostProduct, Long> {

    @Query("select pp from PostProduct pp " +
            "join fetch pp.product " +
            "where pp.post.id = :postId")
    List<PostProduct> findByPostId(Long postId);

    // 제품 ID에 대해 연결된 PostProduct의 수를 반환
    long countByProductId(Long productId);
}
