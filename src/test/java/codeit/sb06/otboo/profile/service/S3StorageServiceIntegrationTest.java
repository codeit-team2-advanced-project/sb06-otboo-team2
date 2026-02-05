package codeit.sb06.otboo.profile.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requires AWS env values and a reachable S3 bucket")
@SpringBootTest
class S3StorageServiceIntegrationTest {

    @Autowired
    private S3StorageService s3StorageService;

    @Test
    void putGetPresignedUrlAndDelete() {
        String id = "test-profile-" + UUID.randomUUID();
        s3StorageService.putObject(id, "test-data".getBytes(StandardCharsets.UTF_8));

        String presignedUrl = s3StorageService.getPresignedUrl(id);
        assertNotNull(presignedUrl);

        s3StorageService.deleteObject(id);
    }
}
