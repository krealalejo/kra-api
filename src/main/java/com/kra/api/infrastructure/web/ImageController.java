package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.s3.S3Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> streamImage(HttpServletRequest request) throws IOException {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String key = fullPath.replaceFirst("^/images/", "");

        if (key.isBlank() || key.contains("..")) {
            return ResponseEntity.badRequest().build();
        }

        try (ResponseInputStream<GetObjectResponse> s3Stream = s3Service.streamObject(key)) {
            String contentType = s3Stream.response().contentType();
            MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
            byte[] bytes = s3Stream.readAllBytes();
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header("Cache-Control", "public, max-age=86400")
                    .body(bytes);
        } catch (NoSuchKeyException _) {
            return ResponseEntity.notFound().build();
        }
    }
}
