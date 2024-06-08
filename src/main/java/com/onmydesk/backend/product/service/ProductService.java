package com.onmydesk.backend.product.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.repository.PostProductRepository;
import com.onmydesk.backend.product.dto.*;
import com.onmydesk.backend.product.mapper.PageMapper;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.repository.PageRepository;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.error.errorcode.ProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.wish.domain.Wish;
import com.onmydesk.backend.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PageRepository pageRepository;
    private final WishRepository wishRepository;
    private final MemberService memberService;
    private final ProductSearchAndCrawlingService productSearchAndCrawlingService;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;

    // 상품 목록 조회
    @Cacheable(value = "products", key = "#page + '-' + #limit + '-' + #criteria")
    public List<ProductResponse> getList(Integer page, Integer limit, Integer criteria) {
        String sortProperty = switch (criteria) {
            case 1 -> "postCount";
            case 2 -> "wishCount";
            case 3 -> "viewCount";
            default -> throw new IllegalArgumentException();
        };

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.DESC, sortProperty);
        org.springframework.data.domain.Page<Product> productPage = productRepository.findAll(pageable);

        // 로그인 되어 있으면 목록에서 유저가 찜 눌렀는지 여부 추가
        try {
            Member member = memberService.getMember();
            return productPage.stream()
                    .map(product -> {
                        boolean isWished = wishRepository.findByMemberAndProduct(member, product).isPresent();
                        return productMapper.toResponse(product, isWished);
                    })
                    .collect(Collectors.toList());

            // 인증 실패 시 전부 누르지 않은 것으로 처리
        } catch (Exception e) {
            return productPage.stream()
                    .map(product -> productMapper.toResponse(product, false))
                    .collect(Collectors.toList());
        }
    }

    // 상품 개별 조회
    public ProductAndPageResponse getFind(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        productRepository.addViewCount(product);

        List<Page> pages = pageRepository.findByProductId(productId);

        List<PageResponse> pageResponses = pages.stream()
                .map(pageMapper::toResponse)
                .collect(Collectors.toList());

        try {
            Member member = memberService.getMember();
            boolean isWished = wishRepository.findByMemberAndProduct(member, product).isPresent();
            ProductResponse productResponse = productMapper.toResponse(product, isWished);

            return ProductAndPageResponse.builder()
                    .product(productResponse)
                    .pages(pageResponses)
                    .build();
        } catch (Exception e) {
            ProductResponse productResponse = productMapper.toResponse(product, false);

            return ProductAndPageResponse.builder()
                    .product(productResponse)
                    .pages(pageResponses)
                    .build();
        }
    }

    // 상품 저장
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(ProductRequest productRequest) {
        // 기존에 동일한 productCode를 가진 상품이 있는지 확인
        Optional<Product> existingProduct = productRepository.findByProductCode(productRequest.getProductCode());
        if (existingProduct.isPresent()) {
            // 기존 상품이 있으면 해당 상품을 반환
            return existingProduct.get();
        } else {
            Product product = productMapper.toProductEntity(productRequest);
            productRepository.save(product);
            // 크롤링을 통해 페이지 정보 가져옴
            List<Page> pages = productSearchAndCrawlingService.crawlPage(product);
            for (Page page : pages) {
                page.setProduct(product);
                pageRepository.save(page);
            }
            return product;
        }
    }

    // 찜한 상품 조회
    public List<ProductResponse> getWishProduct() {
        Member member = memberService.getMember();
        List<Wish> wish = wishRepository.findAllByMember(member);
        List<Product> products = wish.stream().map(productRepository::findByWish).toList();
        return products.stream()
                .map(product -> productMapper.toResponse(product, true))
                .collect(Collectors.toList());
    }


//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul") : 테스팅용 1분 스케줄러
// 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateProductPricesDaily() {
        List<Product> allProducts = productRepository.findAll();
        allProducts.forEach(product -> {
            updateProductPrice(product);
        });
        log.info("상품 가격 및 관련 게시글 가격이 업데이트 되었습니다.");
    }
    private void updateProductPrice(Product product) {
        try {
            String jsonResponse = productSearchAndCrawlingService.searchProduct(product.getProductName(), 10, 1);
            JsonElement jsonElement = JsonParser.parseString(jsonResponse);
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    int updatedPrice = jsonObject.get("lprice").getAsInt();
                    if (product.getLprice() != updatedPrice) {
                        product.setLprice(updatedPrice);
                        productRepository.save(product);
                        // 가격이 업데이트 된 후 관련 페이지 데이터 갱신
                        List<Page> updatedPages = productSearchAndCrawlingService.crawlPage(product);
                        List<Page> existingPages = pageRepository.findByProductId(product.getId());
                        if (!existingPages.isEmpty()) {
                            pageRepository.deleteAll(existingPages);
                        }
                        updatedPages.forEach(pageRepository::save);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(product.getId() + "의 상품을 업데이트하는데 실패했습니다. 에러 원인: " + e.getMessage());
        }
    }


}
