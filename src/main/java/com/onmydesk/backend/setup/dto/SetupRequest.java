package com.onmydesk.backend.setup.dto;

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
@Schema(description = "셋업 생성할 때 필요한 데이터")
public class SetupRequest {

    @NotBlank
    @Schema(description = "셋업 이름", nullable = false, example = "setup name")
    private String setupName;

    private List<ProductRequest> products;
}
