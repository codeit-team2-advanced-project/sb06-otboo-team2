package codeit.sb06.otboo.notification.service;

import codeit.sb06.otboo.config.JacksonConfig;
import codeit.sb06.otboo.notification.config.EmbeddedRedisConfig;
import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.enums.NotificationLevel;
import codeit.sb06.otboo.notification.mapper.NotificationMapper;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.notification.service.impl.NotificationCacheServiceImpl;
import codeit.sb06.otboo.notification.util.SseEventIdGenerator;
import codeit.sb06.otboo.util.EasyRandomUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataRedisTest
@Import({JacksonConfig.class,
        EmbeddedRedisConfig.class,
        NotificationCacheServiceImpl.class,
        SseEventIdGenerator.class
})
@ActiveProfiles("test")
class NotificationCacheServiceRedisTest {

    private final EasyRandom easyRandom = EasyRandomUtil.getRandom();

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private NotificationCacheServiceImpl notificationCacheService;
    @Autowired
    private SseEventIdGenerator sseEventIdGenerator;
    @MockitoBean
    private NotificationRepository notificationRepository;
    @MockitoBean
    private NotificationMapper notificationMapper;


    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("notifications:user:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("알림 캐시 저장 테스트")
    void saveNotificationToCacheTest() {
        // given
        NotificationDto dto = easyRandom.nextObject(NotificationDto.class);
        String key = "notifications:user:" + dto.receiverId();

        // when
        notificationCacheService.save(dto);

        // then
        List<String> results = redisTemplate.opsForList().range(key, 0, -1);
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0)).contains(dto.content())
        );
    }

    @Test
    @DisplayName("알림 캐시 조회 테스트")
    void getRecentNotificationsFromCacheTest() {
        // given
        UUID userId = UUID.randomUUID();
        for (int i = 0; i < 5; i++) {
            NotificationDto dto = NotificationDto.builder()
                    .id(UUID.randomUUID())
                    .receiverId(userId)
                    .title("알림 제목 " + i)
                    .content("알림 내용 " + i)
                    .level(NotificationLevel.INFO)
                    .build();
            notificationCacheService.save(dto);
        }

        // when
        List<NotificationDto> dtoList = notificationCacheService.getRecentNotifications(userId);

        // then
        assertAll(
                () -> assertThat(dtoList).hasSize(5),
                () -> assertThat(dtoList.get(0).content()).isEqualTo("알림 내용 4"),
                () -> assertThat(dtoList.get(4).content()).isEqualTo("알림 내용 0")
        );
    }

    @Test
    @DisplayName("lastEventId 이후 알림 캐시 조회 테스트")
    void getNotificationsAfterFromCacheTest() {
        // given
        UUID userId = UUID.randomUUID();
        String lastEventId = null;
        for (int i = 0; i < 5; i++) {
            NotificationDto dto = NotificationDto.builder()
                    .id(UUID.randomUUID())
                    .createdAt(LocalDateTime.now())
                    .receiverId(userId)
                    .title("알림 제목 " + i)
                    .content("알림 내용 " + i)
                    .level(NotificationLevel.INFO)
                    .build();
            notificationCacheService.save(dto);
            if (i == 2) {
                lastEventId = sseEventIdGenerator.generator(dto.createdAt(), userId);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // when
        List<NotificationDto> dtoList = notificationCacheService.getNotificationsAfter(userId, lastEventId);

        // then
        assertAll(
                () -> assertThat(dtoList).hasSize(2),
                () -> assertThat(dtoList.get(0).content()).isEqualTo("알림 내용 4"),
                () -> assertThat(dtoList.get(1).content()).isEqualTo("알림 내용 3")
        );
    }
}
