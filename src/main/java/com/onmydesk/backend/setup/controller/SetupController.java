package com.onmydesk.backend.setup.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.setup.dto.SetupAndProductResponse;
import com.onmydesk.backend.setup.dto.SetupRequest;
import com.onmydesk.backend.setup.dto.SetupResponse;
import com.onmydesk.backend.setup.service.SetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "셋업", description = "셋업 API")
public class SetupController {

    private final SetupService setupService;
    private final ApiResponse apiResponse;

    // 셋업 생성
    @PostMapping("/setups")
    @Operation(summary = "셋업 생성", description = "새로운 셋업을 생성한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성"))
    public ResponseEntity<?> createSetup(@RequestBody SetupRequest request) {
        setupService.saveSetup(request);
        return apiResponse.success("셋업 생성 성공", HttpStatus.CREATED);
    }

    // 셋업 목록 조회
    @GetMapping("/setups")
    @Operation(summary = "셋업 목록 조회", description = "셋업 목록을 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer limit) {
        List<SetupResponse> setupResponses = setupService.list(page, limit);
        return apiResponse.success("셋업 목록 조회 성공", setupResponses, HttpStatus.OK);
    }


    // 셋업 상세 조회
    @GetMapping("/setups/{setupId}")
    @Operation(summary = "셋업 상세 조회", description = "셋업 상세 정보를 조회한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> getPost(@PathVariable("setupId") Long setupId) {
        SetupAndProductResponse setupAndProductResponse = setupService.find(setupId);
        return apiResponse.success("셋업 조회 성공", setupAndProductResponse, HttpStatus.OK);
    }

    // 셋업 업데이트
    @PutMapping("/setups/{setupId}")
    @Operation(summary = "셋업 업데이트", description = "셋업 정보를 수정한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<?> updateSetup(@PathVariable("setupId") Long setupId,
                                         @RequestBody SetupRequest request) {
        SetupResponse setupResponse = setupService.update(setupId, request);
        return apiResponse.success("셋업 업데이트 성공", setupResponse, HttpStatus.OK);
    }

    // 셋업 삭제
    @DeleteMapping("/setups/{setupId}")
    @Operation(summary = "셋업 삭제", description = "셋업을 삭제한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제"))
    public ResponseEntity<?> deletePost(@PathVariable("setupId") Long setupId) {
        setupService.delete(setupId);
        return apiResponse.success("셋업 삭제 성공", HttpStatus.NO_CONTENT);
    }

    // 셋업 상품 삭제
    @DeleteMapping("/setups/{setupId}/{productId}")
    @Operation(summary = "셋업 상품 삭제", description = "셋업 상품을 삭제한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제"))
    public ResponseEntity<?> deleteProduct(@PathVariable("setupId") Long setupId,
                                           @PathVariable("productId") Long productId) {
        setupService.deleteProduct(setupId, productId);
        return apiResponse.success("셋업 상품 삭제 성공", HttpStatus.NO_CONTENT);
    }

}
