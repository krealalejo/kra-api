package com.kra.api.infrastructure.web.dto;

import com.kra.api.domain.model.BlogPost;

import java.time.Instant;
import java.util.List;

public record BlogPostResponse(
        String slug,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt,
        List<ReferenceDto> references,
        String imageUrl) {

    public record ReferenceDto(String label, String url) {}

    public static BlogPostResponse from(BlogPost post) {
        List<ReferenceDto> refs = post.getReferences().stream()
                .map(r -> new ReferenceDto(r.label(), r.url()))
                .toList();
        return new BlogPostResponse(
                post.getSlug().getValue(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                refs,
                post.getImageUrl());
    }
}
