package com.kra.api.application;

public class BlogPostNotFoundException extends EntityNotFoundException {

    public BlogPostNotFoundException(String slug) {
        super("Blog post not found: " + slug);
    }
}
