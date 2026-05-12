package com.kra.api.infrastructure.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.function.Consumer;

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

    private PresignedPutObjectRequest mockPresigned() throws MalformedURLException {
        PresignedPutObjectRequest presigned = mock(PresignedPutObjectRequest.class);
        when(presigned.url()).thenReturn(URI.create("https://test-url.com").toURL());
        when(s3Presigner.presignPutObject(any(Consumer.class))).thenReturn(presigned);
        return presigned;
    }

    @Test
    void generateUploadUrl_blogImageWithSlug_returnsFinalCoverKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("image.png", "image/png", "blog", "my-post");

        assertNotNull(result.uploadUrl());
        assertEquals("blog/my-post-cover.webp", result.s3Key());
    }

    @Test
    void generateUploadUrl_blogImageNoSlug_usesUuidInFinalKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("image.png", "image/png", null, null);

        assertNotNull(result.uploadUrl());
        assertTrue(result.s3Key().startsWith("blog/"));
        assertTrue(result.s3Key().endsWith("-cover.webp"));
    }

    @Test
    void generateUploadUrl_portraitHomeType_returnsPortraitsHomeKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("photo.jpg", "image/jpeg", "portrait", "home");

        assertEquals("portraits/home.webp", result.s3Key());
    }

    @Test
    void generateUploadUrl_portraitCvType_returnsPortraitsCvKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("photo.jpg", "image/jpeg", "portrait", "cv");

        assertEquals("portraits/cv.webp", result.s3Key());
    }

    @Test
    void generateUploadUrl_portraitWithNullSlug_defaultsToHome() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("photo.jpg", "image/jpeg", "portrait", null);

        assertEquals("portraits/home.webp", result.s3Key());
    }

    @Test
    void generateUploadUrl_pdfContentType_returnsDocumentsCvPdfKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("cv.pdf", "application/pdf", null, null);

        assertEquals("documents/cv.pdf", result.s3Key());
    }

    @Test
    void generateUploadUrl_noExtension_producesValidFinalKey() throws MalformedURLException {
        mockPresigned();

        S3Service.PresignResult result = s3Service.generateUploadUrl("filename", "image/jpeg", "blog", "my-post");

        assertEquals("blog/my-post-cover.webp", result.s3Key());
    }

    @Test
    void generateUploadUrl_invokesPresignConsumerBody() throws MalformedURLException {
        PresignedPutObjectRequest presigned = mock(PresignedPutObjectRequest.class);
        when(presigned.url()).thenReturn(URI.create("https://test-url.com").toURL());
        doAnswer(inv -> {
            Consumer<PutObjectPresignRequest.Builder> consumer = inv.getArgument(0);
            consumer.accept(PutObjectPresignRequest.builder());
            return presigned;
        }).when(s3Presigner).presignPutObject(any(Consumer.class));

        S3Service.PresignResult result = s3Service.generateUploadUrl("file.jpg", "image/jpeg", "blog", "test-slug");

        assertNotNull(result.uploadUrl());
        assertEquals("blog/test-slug-cover.webp", result.s3Key());
    }

    @Test
    void deleteObject_validKey_callsS3DeleteOnce() {
        s3Service.deleteObject("blog/my-post-cover.webp");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());
        assertEquals(BUCKET, captor.getValue().bucket());
        assertEquals("blog/my-post-cover.webp", captor.getValue().key());
    }

    @Test
    void deleteObject_portraitKey_callsS3DeleteOnce() {
        s3Service.deleteObject("portraits/home.webp");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());
        assertEquals("portraits/home.webp", captor.getValue().key());
    }

    @Test
    void deleteObject_pdfKey_callsS3DeleteOnce() {
        s3Service.deleteObject("documents/cv.pdf");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());
        assertEquals("documents/cv.pdf", captor.getValue().key());
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

    @Test
    void streamObject_callsGetObjectWithCorrectBucketAndKey() {
        String key = "blog/my-post-cover.webp";
        GetObjectResponse response = GetObjectResponse.builder().contentType("image/webp").build();
        ResponseInputStream<GetObjectResponse> stream =
                new ResponseInputStream<>(response, AbortableInputStream.create(new ByteArrayInputStream(new byte[0])));

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseInputStream<GetObjectResponse> result = s3Service.streamObject(key);

        ArgumentCaptor<GetObjectRequest> captor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(captor.capture());
        assertEquals(BUCKET, captor.getValue().bucket());
        assertEquals(key, captor.getValue().key());
        assertSame(stream, result);
    }
}
