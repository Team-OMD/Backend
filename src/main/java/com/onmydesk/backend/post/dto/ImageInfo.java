package com.onmydesk.backend.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageInfo {
    private final Long id;
    private final String url;
}
