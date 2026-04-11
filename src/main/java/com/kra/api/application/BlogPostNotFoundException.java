package com.kra.api.application;

public class BlogPostNotFoundException extends RuntimeException {

    public BlogPostNotFoundException(String slug) {
        super("Blog post not found: " + slug);
    }
}
