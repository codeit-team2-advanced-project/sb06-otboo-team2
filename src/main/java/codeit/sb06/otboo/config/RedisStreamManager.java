package codeit.sb06.otboo.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamManager {

    private final StringRedisTemplate redisTemplate;
    private final String serverId;
    private final String streamKey;

    @PostConstruct
    public void init() {
        String groupName = "group-noti-" + serverId;

        try {
            // 스트림이 없으면 생성
            if (!redisTemplate.hasKey(streamKey)) {
                RecordId id = redisTemplate.opsForStream().add(streamKey, Map.of("_init", "true"));
                log.debug("[Redis Stream] 스트림 생성 완료: {}", streamKey);
                if (id != null) {
                    redisTemplate.opsForStream().delete(streamKey, id);
                }
            }
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), groupName);
            log.debug("[Redis Stream] 소비자 그룹 생성 성공: {}", groupName);

        } catch (RedisSystemException e) {
            if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
                log.info("[Redis Stream] 소비자 그룹이 이미 존재합니다: {}", groupName);
            } else {
                log.error("[Redis Stream] 소비자 그룹 생성 중 예외 발생: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("[Redis Stream] 알 수 없는 오류 발생: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            String groupName = "group-noti-" + serverId;
            redisTemplate.opsForStream().destroyGroup(streamKey, groupName);
            log.info(">>>> [Redis Cleanup] 소비자 그룹 삭제 완료: {}", groupName);
        } catch (Exception e) {
            log.error(">>>> [Redis Cleanup] 실패: {}", e.getMessage());
        }
    }
}
