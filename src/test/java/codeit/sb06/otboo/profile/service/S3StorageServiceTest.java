package codeit.sb06.otboo.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.exception.profile.ProfileS3NotFound;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageService(s3Client, s3Presigner);
        ReflectionTestUtils.setField(s3StorageService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(s3StorageService, "presignedUrlExpiration", "PT10M");
    }

    @Test
    void putObjectStoresDataAndReturnsId() {
        String id = "file-id";
        byte[] data = "hello".getBytes();

        String result = s3StorageService.putObject(id, data);

        assertEquals(id, result);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deleteObjectDeletesWhenObjectExists() {
        when(s3Client.headObject(any(java.util.function.Consumer.class)))
            .thenReturn(HeadObjectResponse.builder().build());

        s3StorageService.deleteObject("file-id");

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteObjectThrowsWhenDeleteFails() {
        when(s3Client.headObject(any(java.util.function.Consumer.class)))
            .thenReturn(HeadObjectResponse.builder().build());
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
            .thenThrow(S3Exception.builder().message("delete failed").build());

        assertThrows(ProfileS3NotFound.class, () -> s3StorageService.deleteObject("file-id"));
    }

    @Test
    void deleteObjectThrowsWhenObjectMissing() {
        when(s3Client.headObject(any(java.util.function.Consumer.class)))
            .thenThrow(S3Exception.builder().message("missing").build());

        assertThrows(ProfileS3NotFound.class, () -> s3StorageService.deleteObject("missing-id"));
    }

    @Test
    void getPresignedUrlReturnsUrlWhenObjectExists() throws Exception {
        when(s3Client.headObject(any(java.util.function.Consumer.class)))
            .thenReturn(HeadObjectResponse.builder().build());
        PresignedGetObjectRequest presigned = org.mockito.Mockito.mock(PresignedGetObjectRequest.class);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presigned);
        when(presigned.url()).thenReturn(new URL("https://example.com/file-id"));

        String result = s3StorageService.getPresignedUrl("file-id");

        assertEquals("https://example.com/file-id", result);
    }

    @Test
    void getPresignedUrlThrowsWhenObjectMissing() {
        when(s3Client.headObject(any(java.util.function.Consumer.class)))
            .thenThrow(S3Exception.builder().message("missing").build());

        assertThrows(ProfileS3NotFound.class, () -> s3StorageService.getPresignedUrl("missing-id"));
    }
}
