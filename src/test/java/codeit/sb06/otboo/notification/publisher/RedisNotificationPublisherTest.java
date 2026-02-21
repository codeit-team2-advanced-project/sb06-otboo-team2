package codeit.sb06.otboo.notification.publisher;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.publisher.impl.RedisNotificationPublisherImpl;
import codeit.sb06.otboo.util.EasyRandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisNotificationPublisherTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private StreamOperations<String, String, String> streamOps;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private String notificationStreamKey;

    private RedisNotificationPublisherImpl redisNotificationPublisher;

    @BeforeEach
    void setUp() {
        notificationStreamKey = "notification:stream";
        redisNotificationPublisher = new RedisNotificationPublisherImpl(redisTemplate, objectMapper, notificationStreamKey);
        doReturn(streamOps).when(redisTemplate).opsForStream();
    }


    @Test
    @DisplayName("알림이 Redis 스트림에 발행된다.")
    void publishNotificationTest() {
        // given
        NotificationDto dto = easyRandom.nextObject(NotificationDto.class);

        // when
        redisNotificationPublisher.publish(dto);

        // then
        verify(streamOps, times(1)).add(any(MapRecord.class));
    }
}
