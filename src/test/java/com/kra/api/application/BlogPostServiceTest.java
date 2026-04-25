package com.kra.api.application;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.repository.BlogPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BlogPostServiceTest {

    private final BlogPostRepository repository = mock(BlogPostRepository.class);
    private final BlogPostService service = new BlogPostService(repository);

    @BeforeEach
    void resetMocks() {
        reset(repository);
    }

    @Test
    void createPost_savesAndReturnsPost() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        BlogPost created = service.createPost("my-slug", "My Title", "Content here", List.of());
        verify(repository).save(any(BlogPost.class));
        assertEquals("my-slug", created.getSlug().getValue());
        assertEquals("My Title", created.getTitle());
    }

    @Test
    void createPost_duplicateSlug_throwsIllegalArgumentException() {
        BlogSlug slug = BlogSlug.of("existing-slug");
        BlogPost existing = new BlogPost(slug, "Existing", "", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class,
            () -> service.createPost("existing-slug", "New Title", "Content", List.of()));
    }

    @Test
    void createPost_nullContent_defaultsToEmptyString() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        BlogPost created = service.createPost("my-slug", "Title", null, List.of());
        assertEquals("", created.getContent());
    }

    @Test
    void listPosts_returnsPostsFromRepository() {
        List<BlogPost> posts = List.of(
            new BlogPost(BlogSlug.of("post-1"), "Title 1", "", Instant.now(), Instant.now(), List.of(), null),
            new BlogPost(BlogSlug.of("post-2"), "Title 2", "", Instant.now(), Instant.now(), List.of(), null)
        );
        when(repository.findAllByNewestFirst()).thenReturn(posts);
        List<BlogPost> result = service.listPosts();
        assertEquals(2, result.size());
        verify(repository).findAllByNewestFirst();
    }

    @Test
    void listPosts_emptyRepository_returnsEmptyList() {
        when(repository.findAllByNewestFirst()).thenReturn(List.of());
        List<BlogPost> result = service.listPosts();
        assertTrue(result.isEmpty());
    }

    @Test
    void createPost_setsCreatedAtAndUpdatedAt() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        BlogPost created = service.createPost("new-post", "Title", "Content", List.of());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
    }

    @Test
    void getPost_found_returnsPost() {
        BlogSlug slug = BlogSlug.of("my-post");
        BlogPost post = new BlogPost(slug, "My Post", "Content", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(post));
        BlogPost result = service.getPost("my-post");
        assertEquals("my-post", result.getSlug().getValue());
    }

    @Test
    void getPost_notFound_throwsBlogPostNotFoundException() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        assertThrows(BlogPostNotFoundException.class,
            () -> service.getPost("nonexistent"));
    }

    @Test
    void updatePost_found_updatesAndReturns() {
        BlogSlug slug = BlogSlug.of("my-post");
        BlogPost existing = new BlogPost(slug, "Old Title", "Old Content", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(existing));
        BlogPost updated = service.updatePost("my-post", "New Title", "New Content", List.of());
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Content", updated.getContent());
        verify(repository).save(existing);
    }

    @Test
    void updatePost_notFound_throwsBlogPostNotFoundException() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        assertThrows(BlogPostNotFoundException.class,
            () -> service.updatePost("nonexistent", "Title", "Content", List.of()));
    }

    @Test
    void updatePost_nullContent_defaultsToEmptyString() {
        BlogSlug slug = BlogSlug.of("my-post");
        BlogPost existing = new BlogPost(slug, "Title", "Old Content", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(existing));
        BlogPost updated = service.updatePost("my-post", "Title", null, List.of());
        assertEquals("", updated.getContent());
    }

    @Test
    void deletePost_found_deletesSuccessfully() {
        BlogSlug slug = BlogSlug.of("my-post");
        BlogPost existing = new BlogPost(slug, "Title", "", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(existing));
        assertDoesNotThrow(() -> service.deletePost("my-post"));
        verify(repository).deleteBySlug(slug);
    }

    @Test
    void deletePost_notFound_throwsBlogPostNotFoundException() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        assertThrows(BlogPostNotFoundException.class,
            () -> service.deletePost("nonexistent"));
    }

    @Test
    void createPost_nullReferences_defaultsToEmptyList() {
        when(repository.findBySlug(any())).thenReturn(Optional.empty());
        BlogPost created = service.createPost("my-slug", "Title", "Content", null);
        assertTrue(created.getReferences().isEmpty());
        verify(repository).save(any(BlogPost.class));
    }

    @Test
    void updatePost_nullReferences_defaultsToEmptyList() {
        BlogSlug slug = BlogSlug.of("my-post");
        BlogPost existing = new BlogPost(slug, "Title", "Content", Instant.now(), Instant.now(), List.of(), null);
        when(repository.findBySlug(slug)).thenReturn(Optional.of(existing));
        BlogPost updated = service.updatePost("my-post", "Title", "Content", null);
        assertTrue(updated.getReferences().isEmpty());
    }
}
