package com.onmydesk.backend.s3.controller;

import com.onmydesk.backend.global.ApiResponse;
import com.onmydesk.backend.s3.S3Uploader;
import com.onmydesk.backend.s3.domain.Image;
import com.onmydesk.backend.s3.repository.ImageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
@Tag(name = "이미지 업로드", description = "이미지 업로드 API")
public class ImageController {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;
    private final ApiResponse apiResponse;



    @PostMapping("/upload")
    @Operation(summary = "이미지 업로드", description = "이미지는 S3에 업로드한다.")
    @ApiResponses(value = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성"))
    public ResponseEntity<Object> uploadImage(@RequestParam("images") List<MultipartFile> multipartFiles) {
        List<Image> uploadedImages = new ArrayList<>();
        try {
            for (MultipartFile file : multipartFiles) {
                String dirName = "images"; // S3에서 사용할 디렉토리 이름
                String imageUrl = s3Uploader.upload(file, dirName);
                Image image = new Image();
                image.setUrl(imageUrl);
                image.setThumbnail(false); // 썸네일 여부 설정 필요 시 로직 추가
                uploadedImages.add(imageRepository.save(image));
            }
            return apiResponse.success("이미지 업로드 성공", uploadedImages, HttpStatus.OK);
        } catch (Exception e) {
            return apiResponse.error("이미지 업로드 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
