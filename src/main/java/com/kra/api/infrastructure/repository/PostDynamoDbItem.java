package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.BlogPost;
import com.kra.api.domain.model.BlogSlug;
import com.kra.api.domain.model.Reference;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.List;

@DynamoDbBean
public class PostDynamoDbItem {

    private String pk;
    private String sk;
    private String gsi1pk;
    private String title;
    private String content;
    private Long createdAtMillis;
    private Long updatedAtMillis;
    private List<ReferenceItem> references;
    private String imageUrl;

    public PostDynamoDbItem() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    @DynamoDbAttribute("GSI1PK")
    public String getGsi1pk() {
        return gsi1pk;
    }

    public void setGsi1pk(String gsi1pk) {
        this.gsi1pk = gsi1pk;
    }

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
