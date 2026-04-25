package com.kra.api.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
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

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region:eu-west-1}")
    private String region;

    public record PresignResult(String uploadUrl, String s3Key) {}

    public PresignResult generateUploadUrl(String filename, String contentType) {
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1)
                : "bin";
        String key = "images/" + UUID.randomUUID() + "." + ext;

        try (S3Presigner presigner = createPresigner()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);
            return new PresignResult(presigned.url().toString(), key);
        }
    }

    public void deleteObject(String key) {
        if (key == null || key.isBlank()) return;

        try (S3Client s3Client = createClient()) {
            // Delete original image
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            // Delete thumbnail (derived from key)
            // e.g. images/uuid.jpg -> thumbnails/uuid-thumb.webp
            String thumbKey = key.replaceFirst("^images/", "thumbnails/")
                               .replaceFirst("\\.[^.]+$", "-thumb.webp");

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(thumbKey)
                    .build());
        }
    }

    private S3Presigner createPresigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    private S3Client createClient() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }
}
