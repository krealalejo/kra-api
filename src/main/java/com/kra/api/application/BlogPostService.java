package com.kra.api.application;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public BlogPost createPost(String slug, String title, String content) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        if (blogPostRepository.findBySlug(blogSlug).isPresent()) {
            throw new IllegalArgumentException("Slug already in use");
        }
        Instant now = Instant.now();
        BlogPost post = new BlogPost(blogSlug, title, content != null ? content : "", now, now);
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

    public BlogPost updatePost(String slug, String title, String content) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        BlogPost existing = blogPostRepository.findBySlug(blogSlug)
                .orElseThrow(() -> new BlogPostNotFoundException(slug));
        existing.setTitle(title);
        existing.setContent(content != null ? content : "");
        existing.touchUpdatedAt(Instant.now());
        blogPostRepository.save(existing);
        return existing;
    }

    public void deletePost(String slug) {
        BlogSlug blogSlug = BlogSlug.of(slug);
        if (blogPostRepository.findBySlug(blogSlug).isEmpty()) {
            throw new BlogPostNotFoundException(slug);
        }
        blogPostRepository.deleteBySlug(blogSlug);
    }
}
