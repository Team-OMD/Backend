package com.onmydesk.backend.mocktest;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
