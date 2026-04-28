package com.kra.api.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public record PresignResult(String uploadUrl, String s3Key) {}

    public PresignResult generateUploadUrl(String filename, String contentType) {
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1)
                : "bin";
        String prefix = "application/pdf".equals(contentType) ? "documents" : "images";
        String key = prefix + "/" + UUID.randomUUID() + "." + ext;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return new PresignResult(presigned.url().toString(), key);
    }

    public void deleteObject(String key) {
        if (key == null || key.isBlank()) return;

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());

        String thumbKey = key.replaceFirst("^images/", "thumbnails/")
                           .replaceFirst("\\.[^.]+$", "-thumb.webp");

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(thumbKey)
                .build());
    }
}
