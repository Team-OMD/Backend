package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.product.dto.ProductRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "게시글 생성할 때 필요한 데이터")
public class PostRequest {

    @NotBlank
    @Schema(description = "게시글 제목", nullable = false, example = "title")
    private String title;

    @NotBlank
    @Schema(description = "게시글 내용", example = "content")
    private String content;

    private List<ProductRequest> products;

    private List<Long> imageIds;
    private Long thumbnailImageId; // 썸네일로 지정된 이미지 ID

}
