package com.kra.api.infrastructure.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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

    /**
     * Generates a presigned PUT URL for the staging area and returns the final processed key.
     *
     * S3 layout:
     *   uploads/blog/{slug}.{ext}       — staging (Lambda deletes after processing)
     *   uploads/portraits/{type}.{ext}  — staging (Lambda deletes after processing)
     *   blog/{slug}-cover.webp          — final processed blog cover
     *   portraits/{type}.webp           — final processed portrait
     *   documents/cv.pdf                — CV PDF (direct upload, no Lambda)
     *
     * @param entitySlug blog slug for blog images; "home" or "cv" for portraits; ignored for PDF
     */
    public PresignResult generateUploadUrl(String filename, String contentType, String uploadType, String entitySlug) {
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()
                : "bin";

        String stagingKey;
        String finalKey;

        if ("application/pdf".equals(contentType)) {
            stagingKey = "documents/cv.pdf";
            finalKey = "documents/cv.pdf";
        } else if ("portrait".equals(uploadType)) {
            String type = "cv".equals(entitySlug) ? "cv" : "home";
            stagingKey = "uploads/portraits/" + type + "." + ext;
            finalKey = "portraits/" + type + ".webp";
        } else {
            String slug = (entitySlug != null && !entitySlug.isBlank()) ? entitySlug : UUID.randomUUID().toString();
            stagingKey = "uploads/blog/" + slug + "." + ext;
            finalKey = "blog/" + slug + "-cover.webp";
        }

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(o -> o
                        .bucket(bucketName)
                        .key(stagingKey)
                        .contentType(contentType)));
        return new PresignResult(presigned.url().toString(), finalKey);
    }

    public ResponseInputStream<GetObjectResponse> streamObject(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public void deleteObject(String key) {
        if (key == null || key.isBlank()) return;
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }
}
