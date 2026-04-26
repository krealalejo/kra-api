package com.kra.api.infrastructure.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3Service s3Service;

    private static final String BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client, s3Presigner);
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET);
    }

    @Test
    void generateUploadUrl_withExtension() throws MalformedURLException {
        PresignedPutObjectRequest presigned = mock(PresignedPutObjectRequest.class);
        when(presigned.url()).thenReturn(new URL("https://test-url.com"));
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presigned);

        S3Service.PresignResult result = s3Service.generateUploadUrl("image.png", "image/png");

        assertNotNull(result.uploadUrl());
        assertTrue(result.s3Key().startsWith("images/"));
        assertTrue(result.s3Key().endsWith(".png"));
        verify(s3Presigner).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    void generateUploadUrl_noExtension() throws MalformedURLException {
        PresignedPutObjectRequest presigned = mock(PresignedPutObjectRequest.class);
        when(presigned.url()).thenReturn(new URL("https://test-url.com"));
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presigned);

        S3Service.PresignResult result = s3Service.generateUploadUrl("filename", "application/octet-stream");

        assertTrue(result.s3Key().endsWith(".bin"));
    }

    @Test
    void deleteObject_validKey() {
        String key = "images/uuid.jpg";
        
        s3Service.deleteObject(key);

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(2)).deleteObject(captor.capture());

        assertEquals(BUCKET, captor.getAllValues().get(0).bucket());
        assertEquals("images/uuid.jpg", captor.getAllValues().get(0).key());
        
        assertEquals(BUCKET, captor.getAllValues().get(1).bucket());
        assertEquals("thumbnails/uuid-thumb.webp", captor.getAllValues().get(1).key());
    }

    @Test
    void deleteObject_nullKey_doesNothing() {
        s3Service.deleteObject(null);
        verifyNoInteractions(s3Client);
    }

    @Test
    void deleteObject_blankKey_doesNothing() {
        s3Service.deleteObject("  ");
        verifyNoInteractions(s3Client);
    }
}
