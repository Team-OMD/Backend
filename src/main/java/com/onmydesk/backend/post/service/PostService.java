package com.onmydesk.backend.post.service;

import com.onmydesk.backend.error.errorcode.ImageErrorCode;
import com.onmydesk.backend.error.errorcode.PostErrorCode;
import com.onmydesk.backend.error.exception.RestApiException;
import com.onmydesk.backend.heart.domain.Heart;
import com.onmydesk.backend.heart.repository.HeartRepository;
import com.onmydesk.backend.member.domain.Member;
import com.onmydesk.backend.member.service.MemberService;
import com.onmydesk.backend.post.domain.Post;
import com.onmydesk.backend.post.domain.PostProduct;
import com.onmydesk.backend.post.dto.PostAndProductResponse;
import com.onmydesk.backend.post.dto.PostPreviewResponse;
import com.onmydesk.backend.post.dto.PostRequest;
import com.onmydesk.backend.post.dto.PostResponse;
import com.onmydesk.backend.post.mapper.PostMapper;
import com.onmydesk.backend.post.repository.PostProductRepository;
import com.onmydesk.backend.post.repository.PostRepository;
import com.onmydesk.backend.product.domain.Product;
import com.onmydesk.backend.product.dto.ProductRequest;
import com.onmydesk.backend.product.dto.ProductResponse;
import com.onmydesk.backend.product.mapper.ProductMapper;
import com.onmydesk.backend.product.repository.ProductRepository;
import com.onmydesk.backend.product.service.ProductService;
import com.onmydesk.backend.s3.domain.Image;
import com.onmydesk.backend.s3.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private final ImageRepository imageRepository;
    private final ViewCountService viewCountService;


    // 게시글 생성
    @Transactional
    public Post savePost(PostRequest request) {
        Member member = memberService.getMember();
        Post post = postRepository.save(postMapper.toPostEntity(request, member));

        // ProductRequest 리스트를 순회하며 각 상품 처리
        for (ProductRequest productRequest : request.getProducts()) {
            Product product = productService.saveProduct(productRequest);
            productRepository.addPostCount(product);
            postProductRepository.save(postMapper.toPostProductEntity(post, product));
        }

        if (request.getThumbnailImageId() != null) {
            // 썸네일로 지정된 이미지 처리
            Image thumbnailImage = imageRepository.findById(request.getThumbnailImageId())
                    .orElseThrow(() -> new IllegalArgumentException("헤딩 썸네일 이미지 ID가 존재하지 않습니다."));
            post.setThumbnailImage(thumbnailImage);
        }

        // 기타 로직, 예를 들어 게시글과 이미지 연결 처리
        for (Long imageId : request.getImageIds()) {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지 ID가 존재하지 않습니다."));
            post.addImage(image);
        }

        return post;
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostPreviewResponse> list(Integer page, Integer limit, Integer criteria) {
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
                        return postMapper.toPreviewResponse(post, isLiked);
                    })
                    .collect(Collectors.toList());

        // 인증 실패 시 전부 누르지 않은 것으로 처리
        } catch (Exception e) {
            return postPage.stream()
                    .map(post -> postMapper.toPreviewResponse(post, false))
                    .collect(Collectors.toList());
        }
    }

    // 게시글 단일 조회
    @Transactional
    public PostAndProductResponse getPost(Long postId, String clientIp) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        viewCountService.updateViewCount(postId, clientIp);

        List<PostProduct> postProducts = postProductRepository.findByPostId(postId);

        List<Product> products = postProducts.stream()
                .map(PostProduct::getProduct)
                .collect(Collectors.toList());

        List<ProductResponse> productResponses = products.stream()
                .map(product -> productMapper.toResponse(product, false)) // 기본적으로 false를 사용
                .collect(Collectors.toList());

        try {
            Member member = memberService.getMember();
            boolean isLiked = heartRepository.findByMemberAndPost(member, post).isPresent();
            PostResponse postResponse = postMapper.toResponse(post, isLiked);

            return PostAndProductResponse.builder()
                    .post(postResponse)
                    .products(productResponses)
                    .build();

        } catch (Exception e) {
            PostResponse postResponse = postMapper.toResponse(post, false);

            return PostAndProductResponse.builder()
                    .post(postResponse)
                    .products(productResponses)
                    .build();
        }
    }


    // 게시글 업데이트
    @Transactional
    public PostResponse update(Long postId, PostRequest request) {
        Member member = memberService.getMember();
        Post post = postValidator.validatePostOwnership(postId, member);
        boolean isLiked = heartRepository.findByMemberAndPost(member, post).isPresent();

        // 요청된 상품들의 가격을 누적하여 전체 비용 계산
        int totalPrice = request.getProducts().stream()
                .mapToInt(ProductRequest::getLprice)
                .sum();

        // 게시물의 전체 비용 업데이트
        post.update(request.getTitle(), request.getContent(), totalPrice);

        Set<String> requestedProductCodes = request.getProducts().stream()
                .map(ProductRequest::getProductCode)
                .collect(Collectors.toSet());

        // 기존 PostProduct 중 삭제되어야 할 항목 식별 및 처리
        post.getPostProducts().removeIf(postProduct -> {
            boolean toBeDeleted = !requestedProductCodes.contains(postProduct.getProduct().getProductCode());
            if (toBeDeleted) {
                productRepository.subPostCount(postProduct.getProduct());
                postProductRepository.delete(postProduct);
            }
            return toBeDeleted;
        });

        // 새로운 상품 처리
        request.getProducts().forEach(productRequest -> {
            Optional<Product> existingProductOptional = productRepository.findByProductCode(productRequest.getProductCode());
            if (existingProductOptional.isEmpty()) {
                // 새 상품 저장 및 postCount 증가
                Product newProduct = productService.saveProduct(productRequest);
                productRepository.addPostCount(newProduct);
                postProductRepository.save(postMapper.toPostProductEntity(post, newProduct));
            } else {
                // 기존 상품 객체 추출
                Product existingProduct = existingProductOptional.get();
                // 기존 상품이 새로 연결되었는지 확인
                boolean isNewLink = post.getPostProducts().stream()
                        .noneMatch(pp -> pp.getProduct().getProductCode().equals(existingProduct.getProductCode()));
                if (isNewLink) {
                    productRepository.addPostCount(existingProduct);
                    postProductRepository.save(postMapper.toPostProductEntity(post, existingProduct));
                }
            }
        });

         //이미지 업데이트 로직 추가
        updatePostImages(post, request.getImageIds(), request.getThumbnailImageId());

        return postMapper.toResponse(post, isLiked);
    }

    private void updatePostImages(Post post, List<Long> imageIds, Long thumbnailImageId) {
        List<Image> images = imageRepository.findAllById(imageIds);

        post.getImages().removeIf(image -> !imageIds.contains(image.getId()));


        images.forEach(image -> {
            if (!post.getImages().contains(image)) {
                post.addImage(image);
            }
        });

        if (thumbnailImageId != null) {
            Image thumbnailImage = imageRepository.findById(thumbnailImageId)
                    .orElseThrow(() -> new RestApiException(ImageErrorCode.IMAGE_NOT_FOUND));
            post.setThumbnailImage(thumbnailImage);
        }
    }



    // 게시글 삭제
    public void delete(Long postId) {
        Member member = memberService.getMember();
        Post post = postValidator.validatePostOwnership(postId, member);

        //게시물과 연결된 모든 PostProduct를 가져온다.
        List<PostProduct> postProducts = postProductRepository.findByPostId(postId);
        for (PostProduct postProduct : postProducts) {
            Product product = postProduct.getProduct();
            productRepository.subPostCount(product); // 상품의 postCount 감소
        }

        for (Long productId : postProducts.stream().map(postProduct -> postProduct.getProduct().getId()).toList()) {
            long count = postProductRepository.countByProductId(productId);
            if (count <= 1) {
                // 다른 게시물에 연결된 제품이 없으면 제품 삭제
                productRepository.deleteById(productId);
            }
        }
        postRepository.delete(post);
    }


    // 좋아요 누른 게시물 조회
    public List<PostPreviewResponse> getHeartPost() {
        Member member = memberService.getMember();
        List<Heart> hearts = heartRepository.findAllByMember(member);
        List<Post> posts = hearts.stream().map(postRepository::findByHeart).toList();
        return posts.stream()
                .map(post -> postMapper.toPreviewResponse(post, true))
                .collect(Collectors.toList());
    }
}