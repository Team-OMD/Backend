package com.onmydesk.backend.wish.service;

import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.errorcode.WishErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.wish.domain.Wish;
import com.onmydesk.backend.wish.repository.WishRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;

    @Transactional
    public String insert(Long productId) throws Exception {
        Member member = memberService.getMember();

        Product product = productRepository.findById(productId).orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (wishRepository.findByMemberAndProduct(member, product).isPresent()) {
            throw new RestApiException(WishErrorCode.WISH_ALREADY_EXIST);
        }

        Wish wish = Wish.builder()
                .product(product)
                .member(member)
                .build();

        wishRepository.save(wish);
        productRepository.addWishCount(product);

        return "상품 찜을 눌렀습니다.";
    }

    @Transactional
    public String delete(Long productId) {
        Member member = memberService.getMember();

        Product product = productRepository.findById(productId).orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Wish wish = wishRepository.findByMemberAndProduct(member, product)
                .orElseThrow(() -> new RestApiException(WishErrorCode.WISH_NOT_FOUND));

        wishRepository.delete(wish);
        productRepository.subWishCount(product);

        return "상품 찜을 취소했습니다.";
    }
}
