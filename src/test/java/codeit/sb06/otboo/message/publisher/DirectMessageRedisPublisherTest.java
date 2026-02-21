package codeit.sb06.otboo.message.publisher;

import codeit.sb06.otboo.message.dto.DirectMessageRedisDto;
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
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectMessageRedisPublisherTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private StreamOperations<String, String, String> streamOps;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private String dmStreamKey;

    private DirectMessageRedisPublisher directMessageRedisPublisher;

    @BeforeEach
    void setUp() {
        dmStreamKey = "dm:stream";
        directMessageRedisPublisher = new DirectMessageRedisPublisher(redisTemplate, objectMapper, dmStreamKey);
        doReturn(streamOps).when(redisTemplate).opsForStream();
    }

    @Test
    @DisplayName("DM이 Redis 스트림에 발행된다.")
    void publishDirectMessageTest() {
        // given
        DirectMessageRedisDto dto = easyRandom.nextObject(DirectMessageRedisDto.class);

        // when
        directMessageRedisPublisher.publish(dto);

        // then
        verify(streamOps, times(1)).add(any());
    }
}
