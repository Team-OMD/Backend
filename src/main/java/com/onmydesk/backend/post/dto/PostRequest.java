package com.onmydesk.backend.post.dto;

import com.onmydesk.backend.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "게시글을 생성할 때 필요한 데이터")
public class PostRequest {

    @NotBlank
    @Schema(description = "게시글 제목", nullable = false, example = "title")
    private String title;

    @Schema(description = "게시글 내용", example = "content")
    private String content;
}
