package com.onmydesk.backend.post.service;

import com.onmydesk.backend.error.errorcode.PostErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.domain.PostProduct;
import com.onmydesk.backend.post.dto.PostAndProductResponse;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostProductRepository;
import com.onmydesk.backend.post.repository.PostRepository;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.ProductInfoResponse;
import com.onmydesk.backend.product.dto.ProductRequest;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ProductMapper productMapper;
    private final MemberService memberService;
    private final ProductService productService;
    private final PostValidator postValidator;
    private final HeartRepository heartRepository;
    private final ProductRepository productRepository;
    private final PostProductRepository postProductRepository;


    // 게시글 생성
    @Transactional
    public Post savePost(PostRequest request) {
        Member member = memberService.getMember();
        Post post = postRepository.save(postMapper.toPostEntity(request, member));

        // ProductRequest 리스트를 순회하며 각 상품 처리
        for (ProductRequest productRequest : request.getProducts()) {
            Product product = productService.saveProduct(productRequest);
            postProductRepository.save(postMapper.toPostProductEntity(post, product));
        }
        return post;
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponse> list(Integer page, Integer limit, Integer criteria) {
        String sortProperty = switch (criteria) {
            case 1 -> "createdAt";
            case 2 -> "heartCount";
            case 3 -> "viewCount";
            default -> throw new IllegalArgumentException();
        };

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.Direction.DESC, sortProperty);

        Page<Post> postPage = postRepository.findAll(pageable);

        // 로그인 되어 있으면 목록에서 유저가 좋아요 눌렀는지 여부 추가
        try {
            Member member = memberService.getMember();
            return postPage.stream()
                    .map(post -> {
                        boolean isLiked = heartRepository.findByMemberAndPost(member, post).isPresent();
                        return postMapper.toResponse(post, isLiked);
                    })
                    .collect(Collectors.toList());

        // 인증 실패 시 전부 누르지 않은 것으로 처리
        } catch (Exception e) {
            return postPage.stream()
                    .map(post -> postMapper.toResponse(post, false))
                    .collect(Collectors.toList());
        }
    }

    // 게시글 단일 조회
    @Transactional
    public PostAndProductResponse find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));
        postRepository.addViewCount(post);

        List<PostProduct> postProducts = postProductRepository.findByPostId(postId);

        List<Product> products = postProducts.stream()
                .map(PostProduct::getProduct)
                .collect(Collectors.toList());

        List<ProductInfoResponse> productInfoResponses = products.stream()
                .map(productMapper::toInfoResponse)
                .collect(Collectors.toList());

        try {
            Member member = memberService.getMember();
            boolean isLiked = heartRepository.findByMemberAndPost(member, post).isPresent();
            PostResponse postResponse = postMapper.toResponse(post, isLiked);

            return PostAndProductResponse.builder()
                    .post(postResponse)
                    .products(productInfoResponses)
                    .build();

        } catch (Exception e) {
            PostResponse postResponse = postMapper.toResponse(post, false);

            return PostAndProductResponse.builder()
                    .post(postResponse)
                    .products(productInfoResponses)
                    .build();
        }
    }

    // 게시글 업데이트
    @Transactional
    public PostResponse update(Long postId, PostRequest request) {
        Member member = memberService.getMember();
        Post post = postValidator.validatePostOwnership(postId, member);

        // 요청된 상품들의 가격을 누적하여 전체 비용 계산
        int totalPrice = request.getProducts().stream()
                .mapToInt(ProductRequest::getLprice)
                .sum();

        // 게시물의 전체 비용 업데이트
        post.update(request.getTitle(), request.getContent(), totalPrice);

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
            Product product = productService.saveProduct(productRequest);

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
        Post post = postValidator.validatePostOwnership(postId, member);

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