package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.BlogPost;

import java.time.Instant;

public record BlogPostResponse(
        String slug,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt) {

    public static BlogPostResponse from(BlogPost post) {
        return new BlogPostResponse(
                post.getSlug().getValue(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
