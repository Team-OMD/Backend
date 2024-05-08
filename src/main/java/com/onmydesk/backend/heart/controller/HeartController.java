package com.onmydesk.backend.heart.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.heart.service.HeartService;
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
@Tag(name = "좋아요", description = "좋아요 API")
public class HeartController {
    private final HeartService heartService;
    private final ApiResponse apiResponse;

    @PostMapping("/posts/hearts/{postId}")
    @Operation(summary = "좋아요", description = "게시글에 좋아요를 추가한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> insert(@PathVariable("postId") Long id) throws Exception{
        return apiResponse.success(heartService.insert(id),HttpStatus.OK);
    }

    @DeleteMapping("/posts/hearts/{postId}")
    @Operation(summary = "좋아요 취소", description = "좋아요를 취소한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> delete(@PathVariable("postId") Long id) throws Exception{
        return apiResponse.success(heartService.delete(id),HttpStatus.OK);
    }

}
