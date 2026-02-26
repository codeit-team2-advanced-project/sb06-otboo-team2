package codeit.sb06.otboo.profile.service;

import codeit.sb06.otboo.exception.profile.ProfileS3NotFound;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${spring.cloud.aws.s3.presigned-url-expiration}")
    private String presignedUrlExpiration;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public String putObject(String id, byte[] data){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(id)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

        return id;
    }

    public void deleteObject(String id){

        checkIfObjectExists(id);

        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(id)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e){
            throw new ProfileS3NotFound();
        }
    }

    public String getPresignedUrl(String id) {
        checkIfObjectExists(id);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.parse(presignedUrlExpiration))
            .getObjectRequest(builder -> builder.bucket(bucket).key(id))
            .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }

    private void checkIfObjectExists(String id) {
        try{
            s3Client.headObject(b -> b.bucket(bucket).key(id));
        } catch (S3Exception e){
            throw new ProfileS3NotFound();
        }
    }
}
