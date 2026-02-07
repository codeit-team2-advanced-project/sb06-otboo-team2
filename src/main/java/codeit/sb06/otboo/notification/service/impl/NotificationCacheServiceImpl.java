package codeit.sb06.otboo.notification.service.impl;

import codeit.sb06.otboo.notification.dto.NotificationDto;
import codeit.sb06.otboo.notification.mapper.NotificationMapper;
import codeit.sb06.otboo.notification.repository.NotificationRepository;
import codeit.sb06.otboo.notification.service.NotificationCacheService;
import codeit.sb06.otboo.notification.util.SseEventIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCacheServiceImpl implements NotificationCacheService {

    public static final int TIMEOUT = 5;
    private static final int MAX_NOTIFICATIONS = 50;
    private static final String KEY_PREFIX = "notifications:user:";
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SseEventIdGenerator sseEventIdGenerator;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(UUID userId, NotificationDto dto) {

        String key = KEY_PREFIX + userId;
        try {
            redisTemplate.opsForList().leftPush(key, objectMapper.writeValueAsString(dto));
            redisTemplate.opsForList().trim(key, 0, MAX_NOTIFICATIONS - 1L);
            redisTemplate.expire(key, TIMEOUT, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("Redis 저장 실패", e);
            throw new RuntimeException("Failed to save notification to Redis", e);
        }
    }

    @Override
    public List<NotificationDto> getAllNotifications(UUID userId) {

        String key = KEY_PREFIX + userId;

        List<String> rawList = redisTemplate.opsForList().range(key, 0, -1);

        if (rawList == null || rawList.isEmpty()) {
            PageRequest pageRequest = PageRequest.of(0, MAX_NOTIFICATIONS);
            List<NotificationDto> dtoList = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageRequest)
                    .stream()
                    .map(notificationMapper::toDto)
                    .toList();
            if (!dtoList.isEmpty()) {
                try {
                    redisTemplate.opsForList().rightPushAll(key, objectMapper.writeValueAsString(dtoList.toArray()));
                    redisTemplate.expire(key, TIMEOUT, TimeUnit.DAYS);
                } catch (JsonProcessingException e) {
                    log.error("Redis 저장 실패", e);
                    throw new RuntimeException("Failed to cache notifications to Redis", e);
                }
            }
            return dtoList;
        }

        return rawList.stream()
                .map(NotificationDto.class::cast)
                .toList();
    }

    @Override
    public List<NotificationDto> getNotificationsAfter(UUID userId, String lastEventId) {
        List<NotificationDto> allNotifications = getAllNotifications(userId);

        if (!StringUtils.hasText(lastEventId)) {
            return allNotifications;
        }

        return allNotifications.stream()
                .filter(dto -> {
                    String eventId = sseEventIdGenerator.generator(dto.createdAt(), userId);
                    return eventId.compareTo(lastEventId) > 0;
                })
                .toList();
    }
}
