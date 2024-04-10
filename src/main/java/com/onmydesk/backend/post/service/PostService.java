package com.onmydesk.backend.post.service;

import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.domain.PostProduct;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.exception.PostNotFoundException;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostProductRepository;
import com.onmydesk.backend.post.repository.PostRepository;
import com.onmydesk.backend.product.domain.Page;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.PageRequest;
import com.onmydesk.backend.product.dto.ProductRequest;
import com.onmydesk.backend.product.repository.PageRepository;
import com.onmydesk.backend.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MemberService memberService;
    private final ServiceValidator serviceValidator;
    private final HeartRepository heartRepository;
    private final ProductRepository productRepository;
    private final PageRepository pageRepository;
    private final PostProductRepository postProductRepository;


    // 게시글 생성
    @Transactional
    public Post savePost(PostRequest request) {
        Member member = memberService.getMember();
        Post post = postRepository.save(postMapper.toPostEntity(request, member));

        // ProductRequest 리스트를 순회하며 각 상품 처리
        for (ProductRequest productRequest : request.getProducts()) {
            Optional<Product> existingProduct = productRepository.findByProductCode(productRequest.getProductCode());
            Product product;
            // 상품이 존재하지 않는 경우에만 저장
            if (existingProduct.isEmpty()) {
                product = productRepository.save(postMapper.toProductEntity(productRequest));
                // 페이지 정보 처리
                for (PageRequest pageRequest : productRequest.getPages()) {
                    Page page = postMapper.toPageEntity(pageRequest, product);
                    pageRepository.save(page);
                }
            } else {
                product = existingProduct.get();
            }
            postProductRepository.save(postMapper.toPostProductEntity(post, product));
        }
        return post;
    }

    // 게시글 목록 조회
    public List<PostResponse> list() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 게시글 단일 조회
    public PostResponse find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return postMapper.toResponse(post);
    }

    // 게시글 업데이트
    @Transactional
    public PostResponse update(Long postId, PostRequest request) {
        Member member = memberService.getMember();
        Post post = serviceValidator.validatePostOwnership(postId, member);

        // 요청된 상품들의 가격을 누적하여 전체 비용 계산
        int totalPrice = 0;
        for (ProductRequest productRequest : request.getProducts()) {
            totalPrice += productRequest.getLprice();
        }

        // 게시물의 전체 비용 업데이트
        post.update(request.getTitle(), request.getContent(), totalPrice);

        // 요청된 상품 코드와 기존 상품 코드를 비교하여 처리
        Set<String> requestedProductCodes = request.getProducts().stream()
                .map(ProductRequest::getProductCode)
                .collect(Collectors.toSet());

        // 필요없는 PostProduct 삭제
        post.getPostProducts().removeIf(postProduct -> {
            boolean toBeDeleted = !requestedProductCodes.contains(postProduct.getProduct().getProductCode());
            if (toBeDeleted) {
                postProductRepository.delete(postProduct);
            }
            return toBeDeleted;
        });

        // 새로운 상품 및 페이지 처리
        request.getProducts().forEach(productRequest -> {
            // 상품 조회 또는 생성
            Product product = productRepository.findByProductCode(productRequest.getProductCode())
                    .orElseGet(() -> {
                        Product newProduct = postMapper.toProductEntity(productRequest);

                        // Page 엔티티들을 생성하여 Product에 추가
                        productRequest.getPages().forEach(pageRequest -> {
                            Page newPage = postMapper.toPageEntity(pageRequest, newProduct);
                            pageRepository.save(newPage);
                        });

                        return productRepository.save(newProduct);
                    });

            // 게시글과 상품 연결
            boolean isProductLinked = post.getPostProducts().stream()
                    .anyMatch(pp -> pp.getProduct().getProductCode().equals(product.getProductCode()));

            if (!isProductLinked) {
                PostProduct postProduct = postMapper.toPostProductEntity(post, product);
                postProductRepository.save(postProduct);
            }
        });

        return postMapper.toResponse(post);
    }


    // 게시글 삭제
    public void delete(Long postId) {
        Member member = memberService.getMember();
        Post post = serviceValidator.validatePostOwnership(postId, member);

        //게시물과 연결된 모든 PostProduct를 가져온다.
        List<PostProduct> postProducts = postProductRepository.findByPostId(postId);
        // PostProduct의 ID 목록을 가져온다.
        List<Long> productIds = postProducts.stream()
                .map(postProduct -> postProduct.getProduct().getId())
                .collect(Collectors.toList());

        for (Long productId : productIds) {
            long count = postProductRepository.countByProductId(productId);
            if (count <= 1) {
                // 다른 게시물에 연결된 제품이 없으면 제품 삭제
                productRepository.deleteById(productId);
            }
        }
        postRepository.delete(post);
    }


    // 좋아요 누른 게시물 조회
    public List<PostResponse> getHeartPost() {
        Member member = memberService.getMember();
        List<Heart> heart = heartRepository.findAllByMember(member);
        List<Post> posts = heart.stream().map(postRepository::findByHeart).toList();
        return posts.stream().map(postMapper::toResponse).collect(Collectors.toList());
    }
}