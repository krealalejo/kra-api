package com.kra.api.infrastructure.web;

import com.kra.api.application.BlogPostService;
import com.kra.api.domain.model.BlogPost;
import com.kra.api.infrastructure.web.dto.BlogPostResponse;
import com.kra.api.infrastructure.web.dto.CreateBlogPostRequest;
import com.kra.api.infrastructure.web.dto.UpdateBlogPostRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<BlogPostResponse>> list() {
        List<BlogPostResponse> list = blogPostService.listPosts().stream()
                .map(BlogPostResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<BlogPostResponse> get(@PathVariable String slug) {
        BlogPost post = blogPostService.getPost(slug);
        return ResponseEntity.ok(BlogPostResponse.from(post));
    }

    @PostMapping("/posts")
    public ResponseEntity<BlogPostResponse> create(@Valid @RequestBody CreateBlogPostRequest req) {
        BlogPost created = blogPostService.createPost(
                req.getSlug(),
                req.getTitle(),
                req.getContent() != null ? req.getContent() : "");
        return ResponseEntity.status(HttpStatus.CREATED).body(BlogPostResponse.from(created));
    }

    @PutMapping("/posts/{slug}")
    public ResponseEntity<BlogPostResponse> update(
            @PathVariable String slug,
            @Valid @RequestBody UpdateBlogPostRequest req) {
        BlogPost updated = blogPostService.updatePost(
                slug,
                req.getTitle(),
                req.getContent() != null ? req.getContent() : "");
        return ResponseEntity.ok(BlogPostResponse.from(updated));
    }

    @DeleteMapping("/posts/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug) {
        blogPostService.deletePost(slug);
        return ResponseEntity.noContent().build();
    }
}
