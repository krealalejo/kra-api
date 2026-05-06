package com.kra.api.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

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

    public PresignResult generateUploadUrl(String filename, String contentType, String uploadType) {
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1)
                : "bin";
        String prefix;
        if ("application/pdf".equals(contentType)) {
            prefix = "documents";
        } else if ("portrait".equals(uploadType)) {
            prefix = "images/portraits";
        } else {
            prefix = "images";
        }
        String key = prefix + "/" + UUID.randomUUID() + "." + ext;

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(o -> o
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)));
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
