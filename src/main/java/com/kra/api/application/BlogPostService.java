package com.kra.api.application;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.model.Reference;
import com.kra.api.domain.repository.BlogPostRepository;
import com.kra.api.infrastructure.s3.S3Service;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final S3Service s3Service;

    public BlogPostService(BlogPostRepository blogPostRepository, S3Service s3Service) {
        this.blogPostRepository = blogPostRepository;
        this.s3Service = s3Service;
    }

    public BlogPost createPost(String slug, String title, String content, List<Reference> references) {
        return createPost(slug, title, content, references, null);
    }

    public BlogPost createPost(String slug, String title, String content, List<Reference> references, String imageUrl) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        if (blogPostRepository.findBySlug(blogSlug).isPresent()) {
            throw new IllegalArgumentException("Slug already in use");
        }
        Instant now = Instant.now();
        BlogPost post = new BlogPost(blogSlug, title, content != null ? content : "", now, now,
                references != null ? references : List.of(), imageUrl);
        blogPostRepository.save(post);
        return post;
    }

    public List<BlogPost> listPosts() {
        return blogPostRepository.findAllByNewestFirst();
    }

    public BlogPost getPost(String slug) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        return blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));
    }

    public BlogPost updatePost(String slug, String title, String content, List<Reference> references) {
        return updatePost(slug, title, content, references, null);
    }

    public BlogPost updatePost(String slug, String title, String content, List<Reference> references, String imageUrl) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        BlogPost existing = blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));
        
        // Handle S3 deletion if image is replaced or removed
        String oldImageUrl = existing.getImageUrl();
        if (oldImageUrl != null && !Objects.equals(oldImageUrl, imageUrl)) {
            s3Service.deleteObject(oldImageUrl);
        }

        existing.setTitle(title);
        existing.setContent(content != null ? content : "");
        existing.setReferences(references != null ? references : List.of());
        existing.setImageUrl(imageUrl);   // per D-14: update imageUrl if provided
        existing.touchUpdatedAt(Instant.now());
        blogPostRepository.save(existing);
        return existing;
    }

    public void deletePost(String slug) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        BlogPost existing = blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));
        
        if (existing.getImageUrl() != null) {
            s3Service.deleteObject(existing.getImageUrl());
        }
        
        blogPostRepository.deleteBySlug(blogSlug);
    }
}
