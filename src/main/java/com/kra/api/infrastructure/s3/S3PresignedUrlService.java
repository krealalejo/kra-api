package com.kra.api.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3PresignedUrlService {

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

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build()) {

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
}
