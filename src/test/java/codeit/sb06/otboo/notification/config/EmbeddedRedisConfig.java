package codeit.sb06.otboo.notification.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@Slf4j
@TestConfiguration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @PostConstruct
    public void startRedis() {
        try {
            redisServer = new RedisServer(redisPort);
            redisServer.start();
            log.info("Embedded Redis started on port {}.", redisPort);
        } catch (Exception e) {
            log.error("Embedded Redis 실행 실패: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        try {
            if (redisServer != null) {
                redisServer.stop();
                log.info("Embedded Redis stopped.");
            }
        } catch (Exception e) {
            log.warn("Embedded Redis 종료 중 에러 발생: {}", e.getMessage());
        } finally {
            redisServer = null;
        }
    }
}
