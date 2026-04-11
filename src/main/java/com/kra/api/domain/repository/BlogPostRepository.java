package com.kra.api.domain.repository;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository {

    void save(BlogPost post);

    Optional<BlogPost> findBySlug(BlogSlug slug);

    List<BlogPost> findAllByNewestFirst();

    void deleteBySlug(BlogSlug slug);
}
