package codeit.sb06.otboo.notification.publisher.impl;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.publisher.RedisNotificationPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisNotificationPublisherImpl implements RedisNotificationPublisher {

    public static final int TIMEOUT = 1;
    public static final int COUNT = 30000;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String streamKey;

    @Override
    public void publish(NotificationDto dto) {

        try {
            String jsonPayload = objectMapper.writeValueAsString(dto);
            Map<String, String> map = Collections.singletonMap("payload", jsonPayload);

            MapRecord<String, String, String> record = StreamRecords.newRecord()
                    .in(streamKey)
                    .ofMap(map)
                    .withId(RecordId.autoGenerate());

            redisTemplate.opsForStream().add(record);
            redisTemplate.opsForStream().trim(streamKey, COUNT, true);
            redisTemplate.expire(streamKey, TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize NotificationDto", e);
        }
    }
}
