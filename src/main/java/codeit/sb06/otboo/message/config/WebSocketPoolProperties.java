package codeit.sb06.otboo.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.websocket.pool")
public class WebSocketPoolProperties {

    private PoolSettings inbound;
    private PoolSettings outbound;
    private HeartbeatSettings heartbeat;

    @Getter
    @Setter
    public static class PoolSettings {
        private int coreSize;
        private int maxSize;
        private int queueCapacity;
        private int awaitTerminationSeconds;
    }

    @Getter
    @Setter
    public static class HeartbeatSettings {
        private int poolSize;
        private int awaitTerminationSeconds;
        private int period;
    }
}
