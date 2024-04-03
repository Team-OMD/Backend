package com.onmydesk.backend.mocktest;

import com.onmydesk.backend.page.domain.Page;
import com.onmydesk.backend.product.ProductRepository;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getList_Success_Test() {
        // 가짜 상품 데이터 생성
        Product product1 = Product.builder()
                .img("test1.jpg")
                .productCode("TEST001")
                .brand("Test Brand 1")
                .maker("Test Maker 1")
                .category1("TestCategory1")
                .category2("TestCategory2")
                .category3("TestCategory3")
                .category4("TestCategory4")
                .build();

        Product product2 = Product.builder()
                .img("test2.jpg")
                .productCode("TEST002")
                .brand("Test Brand 2")
                .maker("Test Maker 2")
                .category1("TestCategory1")
                .category2("TestCategory2")
                .category3("TestCategory3")
                .category4("TestCategory4")
                .build();

        // 상품 목록을 반환하도록 설정
        List<Product> testProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(testProducts);

        // 테스트 실행
        List<Product> result = productService.getList();

        // 결과 검증
        assertEquals(2, result.size());
        for (Product product : result) {
            System.out.println("상품 ID: " + product.getId());
            System.out.println("상품 이미지: " + product.getImg());
            System.out.println("상품 코드: " + product.getProductCode());
            System.out.println("브랜드: " + product.getBrand());
            System.out.println("메이커: " + product.getMaker());
            System.out.println("카테고리1: " + product.getCategory1());
            System.out.println("카테고리2: " + product.getCategory2());
            System.out.println("카테고리3: " + product.getCategory3());
            System.out.println("카테고리4: " + product.getCategory4());
            System.out.println("------------------------------------");
        }
    }

    @Test
    void getFind_Success_Test() {
        // 준비
        Product product = Product.builder()
                .id(1L)
                .img("test.jpg")
                .productCode("TEST001")
                .brand("Test Brand 1")
                .maker("Test Maker 1")
                .build();

        Page page1 = Page.builder()
                .id(1L)
                .price(1000)
                .link("https://example1.com")
                .storeName("Test Store1")
                .build();

        Page page2 = Page.builder()
                .id(2L)
                .price(2000)
                .link("https://example2.com")
                .storeName("Test Store2")
                .build();

        // 상품이 존재할 경우를 가정
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // 실행
        Product actualProduct = productService.getFind(1L);

        // 검증
        assertNotNull(actualProduct);
        assertEquals(product.getId(), actualProduct.getId());
        System.out.println(product.getPages());
    }
}
