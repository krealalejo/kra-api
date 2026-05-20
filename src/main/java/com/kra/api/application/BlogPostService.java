package com.kra.api.application;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.model.Reference;
import com.kra.api.domain.repository.BlogPostRepository;
import com.kra.api.infrastructure.s3.S3Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @CacheEvict(value = "posts", allEntries = true)
    public BlogPost createPost(String slug, String title, String content, List<Reference> references) {
        return createPost(slug, title, content, references, null, null);
    }

    @CacheEvict(value = "posts", allEntries = true)
    public BlogPost createPost(String slug, String title, String content, List<Reference> references, String imageUrl) {
        return createPost(slug, title, content, references, imageUrl, null);
    }

    @CacheEvict(value = "posts", allEntries = true)
    public BlogPost createPost(String slug, String title, String content, List<Reference> references, String imageUrl, Instant publishedAt) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        if (blogPostRepository.findBySlug(blogSlug).isPresent()) {
            throw new IllegalArgumentException("Slug already in use");
        }
        Instant now = Instant.now();
        Instant createdAt = publishedAt != null ? publishedAt : now;
        BlogPost post = new BlogPost(blogSlug, title, content != null ? content : "", createdAt, now,
                references != null ? references : List.of(), imageUrl);
        blogPostRepository.save(post);
        return post;
    }

    @Cacheable("posts")
    public List<BlogPost> listPosts() {
        return blogPostRepository.findAllByNewestFirst();
    }

    public List<BlogPost> listAllPostsUncached() {
        return blogPostRepository.findAllByNewestFirst();
    }

    @Cacheable(value = "post", key = "#slug")
    public BlogPost getPost(String slug) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        return blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));
    }

    @Caching(evict = {
        @CacheEvict(value = "post", key = "#slug"),
        @CacheEvict(value = "posts", allEntries = true)
    })
    public BlogPost updatePost(String slug, String title, String content, List<Reference> references) {
        return updatePost(slug, title, content, references, null, null);
    }

    @Caching(evict = {
        @CacheEvict(value = "post", key = "#slug"),
        @CacheEvict(value = "posts", allEntries = true)
    })
    public BlogPost updatePost(String slug, String title, String content, List<Reference> references, String imageUrl) {
        return updatePost(slug, title, content, references, imageUrl, null);
    }

    @Caching(evict = {
        @CacheEvict(value = "post", key = "#slug"),
        @CacheEvict(value = "posts", allEntries = true)
    })
    public BlogPost updatePost(String slug, String title, String content, List<Reference> references, String imageUrl, Instant publishedAt) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        BlogPost existing = blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));

        String oldImageUrl = existing.getImageUrl();
        if (oldImageUrl != null && !Objects.equals(oldImageUrl, imageUrl)) {
            s3Service.deleteObject(oldImageUrl);
        }

        existing.setTitle(title);
        existing.setContent(content != null ? content : "");
        existing.setReferences(references != null ? references : List.of());
        existing.setImageUrl(imageUrl);
        if (publishedAt != null) {
            existing.setCreatedAt(publishedAt);
        }
        existing.touchUpdatedAt(Instant.now());
        blogPostRepository.save(existing);
        return existing;
    }

    @Caching(evict = {
        @CacheEvict(value = "post", key = "#slug"),
        @CacheEvict(value = "posts", allEntries = true)
    })
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
