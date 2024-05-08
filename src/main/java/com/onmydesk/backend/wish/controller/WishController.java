package com.onmydesk.backend.wish.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.wish.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "상품 찜", description = "상품 찜 API")
public class WishController {

    private final WishService wishService;
    private final ApiResponse apiResponse;

    @PostMapping("/products/wish/{productId}")
    @Operation(summary = "찜", description = "상품 찜을 추가한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> insert(@PathVariable("productId") Long id) throws Exception{
        return apiResponse.success(wishService.insert(id), HttpStatus.OK);
    }

    @DeleteMapping("/products/wish/{productId}")
    @Operation(summary = "찜 취소", description = "상품 찜을 취소한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> delete(@PathVariable("productId") Long id) throws Exception{
        return apiResponse.success(wishService.delete(id),HttpStatus.OK);
    }
}
