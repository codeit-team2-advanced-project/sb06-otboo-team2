package codeit.sb06.otboo.storage;

import codeit.sb06.otboo.exception.storage.StorageDeleteFailedException;
import codeit.sb06.otboo.exception.storage.StorageUploadFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Component
public class S3Storage {

    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3Storage(
            @Value("${spring.cloud.aws.s3.bucket}") String bucket,
            @Value("${spring.cloud.aws.region.static}") String region,
            S3Client s3Client
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.region = region;
    }

    /**
     * clothes/{uuid}.{ext} 로 업로드 후 public URL 반환
     */
    public String uploadClothesImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String key = "clothes/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());
        putObject(key, file);
        return getUrl(key);
    }

    public void putObject(String key, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromInputStream(is, file.getSize()));
        } catch (S3Exception e) {
            throw new StorageUploadFailedException(key, e);
        } catch (IOException e) {
            throw new StorageUploadFailedException(key, "IO_ERROR", e);
        }
    }

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        deleteObject(extractKeyFromUrl(url));
    }

    public void deleteObject(String key) {
        if (key == null || key.isBlank()) return;

        try {
            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(req);
        } catch (S3Exception e) {
            throw new StorageDeleteFailedException(key, e);

        }
    }

    public String getUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot < 0) ? "" : filename.substring(dot);
    }

    private String extractKeyFromUrl(String url) {
        // https://{bucket}.s3.{region}.amazonaws.com/{key}
        // 위 형식 가정. 다른 형식(CloudFront 등)이면 여기만 바꾸면 됨.
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        if (!url.startsWith(prefix)) {
            // 형식 다르면 안전하게 예외
            throw new IllegalArgumentException("S3 URL 형식이 예상과 다릅니다: " + url);
        }
        return url.substring(prefix.length());
    }
}
