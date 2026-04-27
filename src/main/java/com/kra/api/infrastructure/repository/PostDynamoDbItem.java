package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.model.Reference;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;
import java.util.List;

@DynamoDbBean
public class PostDynamoDbItem extends AbstractDynamoDbItem {

    private String title;
    private String content;
    private Long createdAtMillis;
    private Long updatedAtMillis;
    private List<ReferenceItem> references;
    private String imageUrl;

    public PostDynamoDbItem() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public void setCreatedAtMillis(Long createdAtMillis) {
        this.createdAtMillis = createdAtMillis;
    }

    public Long getUpdatedAtMillis() {
        return updatedAtMillis;
    }

    public void setUpdatedAtMillis(Long updatedAtMillis) {
        this.updatedAtMillis = updatedAtMillis;
    }

    public List<ReferenceItem> getReferences() { return references; }
    public void setReferences(List<ReferenceItem> references) { this.references = references; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public static PostDynamoDbItem fromDomain(BlogPost post) {
        PostDynamoDbItem item = new PostDynamoDbItem();
        item.setPk("POST#" + post.getSlug().getValue());
        item.setSk("METADATA");
        item.setGsi1pk("TYPE#POST");
        item.setTitle(post.getTitle());
        item.setContent(post.getContent());
        item.setCreatedAtMillis(post.getCreatedAt().toEpochMilli());
        item.setUpdatedAtMillis(post.getUpdatedAt().toEpochMilli());
        item.setReferences(
            post.getReferences().stream()
                .map(r -> new ReferenceItem(r.label(), r.url()))
                .toList()
        );
        item.setImageUrl(post.getImageUrl());
        return item;
    }

    public BlogPost toDomain() {
        String raw = pk.replace("POST#", "");
        BlogSlug slug = BlogSlug.of(raw);
        Instant created = Instant.ofEpochMilli(createdAtMillis != null ? createdAtMillis : 0L);
        Instant updated = Instant.ofEpochMilli(updatedAtMillis != null ? updatedAtMillis : created.toEpochMilli());
        List<Reference> refs = references != null
            ? references.stream()
                  .map(r -> new Reference(r.getLabel(), r.getUrl()))
                  .toList()
            : List.of();
        return new BlogPost(slug, title, content != null ? content : "", created, updated, refs, imageUrl);
    }
}
