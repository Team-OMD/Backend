package com.onmydesk.backend.setup.service;

import com.onmydesk.backend.error.errorcode.SetupErrorCode;
import com.onmydesk.backend.error.errorcode.SetupProductErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.ProductInfoResponse;
import com.onmydesk.backend.product.dto.ProductRequest;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.service.ProductService;
import com.onmydesk.backend.setup.domain.Setup;
import com.onmydesk.backend.setup.domain.SetupProduct;
import com.onmydesk.backend.setup.dto.SetupAndProductResponse;
import com.onmydesk.backend.setup.dto.SetupRequest;
import com.onmydesk.backend.setup.dto.SetupResponse;
import com.onmydesk.backend.setup.mapper.SetupMapper;
import com.onmydesk.backend.setup.repository.SetupProductRepository;
import com.onmydesk.backend.setup.repository.SetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final MemberService memberService;
    private final ProductService productService;
    private final SetupRepository setupRepository;
    private final SetupProductRepository setupProductRepository;
    private final SetupMapper setupMapper;
    private final ProductMapper productMapper;
    private final SetupValidator setupValidator;


    // 셋업 생성
    @Transactional
    public Setup saveSetup(SetupRequest request) {
        Member member = memberService.getMember();
        Setup setup = setupRepository.save(setupMapper.toSetupEntity(request, member));

        // ProductRequest 리스트를 순회하며 각 상품 처리
        for (ProductRequest productRequest : request.getProducts()) {
            Product product = productService.saveProduct(productRequest);
            setupProductRepository.save(setupMapper.toSetupProductEntity(setup, product));
        }
        return setup;
    }


    // 셋업 목록 조회
    @Transactional
    public List<SetupResponse> list(Integer page, Integer limit) {
        Member member = memberService.getMember();

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Setup> setups = setupRepository.findByMember(member, pageable);

        return setups.stream()
                .map(setupMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 셋업 단일 조회
    @Transactional
    public SetupAndProductResponse find(Long setupId) {
        // 셋업 조회
        Setup setup = setupRepository.findById(setupId)
                .orElseThrow(() -> new RestApiException(SetupErrorCode.SETUP_NOT_FOUND));

        // 셋업과 연관된 상품 조회
        List<SetupProduct> setupProducts = setupProductRepository.findBySetupId(setupId);

        List<Product> products = setupProducts.stream()
                .map(SetupProduct::getProduct)
                .collect(Collectors.toList());

        // 상품 정보 변환
        List<ProductInfoResponse> productInfoResponses = products.stream()
                .map(productMapper::toInfoResponse)
                .collect(Collectors.toList());

        // 셋업 응답 생성
        SetupResponse setupResponse = setupMapper.toResponse(setup);

        // SetupAndProductResponse 객체 생성 및 반환
        return SetupAndProductResponse.builder()
                .setup(setupResponse)
                .products(productInfoResponses)
                .build();
    }

    // 셋업 업데이트
    @Transactional
    public SetupResponse update(Long setupId, SetupRequest request) {
        Member member = memberService.getMember();
        Setup setup = setupValidator.validateSetupOwnership(setupId, member);

        // 요청된 상품들의 가격을 누적하여 전체 비용 계산
        int postTotalPrice = request.getProducts().stream()
                .mapToInt(ProductRequest::getLprice)
                .sum();

        // 셋업의 전체 비용 업데이트
        setup.update(request.getSetupName(), postTotalPrice);

        Set<String> requestedProductCodes = request.getProducts().stream()
                .map(ProductRequest::getProductCode)
                .collect(Collectors.toSet());

        // 필요없는 SetupProduct 삭제
        setup.getSetupProducts().removeIf(setupProduct -> {
            boolean toBeDeleted = !requestedProductCodes.contains(setupProduct.getProduct().getProductCode());
            if (toBeDeleted) {
                setupProductRepository.delete(setupProduct);
            }
            return toBeDeleted;
        });

        // 새로운 상품 및 페이지 처리
        request.getProducts().forEach(productRequest -> {
            Product product = productService.saveProduct(productRequest);

            // 셋업과 상품 연결
            boolean isProductLinked = setup.getSetupProducts().stream()
                    .anyMatch(pp -> pp.getProduct().getProductCode().equals(product.getProductCode()));

            if (!isProductLinked) {
                SetupProduct setupProduct = setupMapper.toSetupProductEntity(setup, product);
                setupProductRepository.save(setupProduct);
            }
        });

        return setupMapper.toResponse(setup);
    }

    // 셋업 삭제
    public void delete(Long setupId) {
        Member member = memberService.getMember();
        Setup setup = setupValidator.validateSetupOwnership(setupId, member);

        // 게시물과 연결된 모든 SetupProduct 가져오기
        List<SetupProduct> setupProducts = setupProductRepository.findBySetupId(setupId);

        // 연결된 SetupProduct 삭제
        setupProductRepository.deleteAll(setupProducts);

        // 마지막으로 Setup 삭제
        setupRepository.delete(setup);
    }

    // 셋업 상품 삭제
    public void deleteProduct(Long setupId, Long productId) {
        Member member = memberService.getMember();
        setupValidator.validateSetupOwnership(setupId, member);

        Optional<SetupProduct> setupProduct = setupProductRepository.findBySetupIdAndProductId(setupId, productId);
        if (setupProduct.isPresent()) {
            setupProductRepository.delete(setupProduct.get());
        } else {
            throw new RestApiException(SetupProductErrorCode.SETUP_PRODUCT_NOT_FOUND);
        }
    }
}
