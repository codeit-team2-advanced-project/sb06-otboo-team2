package codeit.sb06.otboo.notification.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class SseEventIdGenerator {

    public String generator(LocalDateTime createdAt, UUID id) {

        if (createdAt == null) {
            return Instant.now().toEpochMilli() + "_" + id.toString();
        }
        return createdAt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli() + "_" + id.toString();
    }
}
