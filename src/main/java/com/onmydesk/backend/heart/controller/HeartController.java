package com.onmydesk.backend.heart.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.heart.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HeartController {
    private final HeartService heartService;
    private final ApiResponse apiResponse;

    @PostMapping("/posts/hearts/{postId}")
    public ResponseEntity<?> insert(@PathVariable("postId") Long id) throws Exception{
        return apiResponse.success(heartService.insert(id),HttpStatus.OK);
    }

    @DeleteMapping("/posts/hearts/{postId}")
    public ResponseEntity<?> delete(@PathVariable("postId") Long id) throws Exception{
        return apiResponse.success(heartService.delete(id),HttpStatus.OK);
    }

}
