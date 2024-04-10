package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.product.dto.ProductRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "게시글 생성할 때 필요한 데이터")
public class PostRequest {

    @NotBlank
    @Schema(description = "게시글 제목", nullable = false, example = "제목")
    private String title;

    @NotBlank
    @Schema(description = "게시글 내용", nullable = false, example = "내용")
    private String content;

    private List<ProductRequest> products;
}
