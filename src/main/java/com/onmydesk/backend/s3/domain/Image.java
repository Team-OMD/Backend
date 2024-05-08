package com.onmydesk.backend.s3.domain;

import com.onmydesk.backend.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private boolean isThumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

}