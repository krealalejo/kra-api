package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.s3.S3Service;
import com.kra.api.infrastructure.web.dto.UploadRequest;
import com.kra.api.infrastructure.web.dto.UploadResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadController {

    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/admin/upload")
    public ResponseEntity<UploadResponse> generateUploadUrl(@Valid @RequestBody UploadRequest request) {
        S3Service.PresignResult result =
                s3Service.generateUploadUrl(request.getFilename(), request.getContentType());
        return ResponseEntity.ok(new UploadResponse(result.uploadUrl(), result.s3Key()));
    }
}
