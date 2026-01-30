package codeit.sb06.otboo.message.config;

import codeit.sb06.otboo.message.handler.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketPoolProperties wsPoolProperties;
    private final WebSocketChannelInterceptor channelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        int heartbeatPeriod = wsPoolProperties.getHeartbeat().getPeriod();

        registry
                .enableSimpleBroker("/sub")
                .setHeartbeatValue(new long[]{heartbeatPeriod, heartbeatPeriod})
                .setTaskScheduler(websocketTaskScheduler());

        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(channelInterceptor)
                .taskExecutor(inboundChannelExecutor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration
                .taskExecutor(outboundChannelExecutor());
    }

    @Bean("websocketTaskScheduler")
    public TaskScheduler websocketTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.setPoolSize(wsPoolProperties.getHeartbeat().getPoolSize());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(wsPoolProperties.getHeartbeat().getAwaitTerminationSeconds());
        scheduler.initialize();
        return scheduler;
    }

    @Bean("websocketInboundExecutor")
    public ThreadPoolTaskExecutor inboundChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ws-inbound-");
        executor.setCorePoolSize(wsPoolProperties.getInbound().getCoreSize());
        executor.setMaxPoolSize(wsPoolProperties.getInbound().getMaxSize());
        executor.setQueueCapacity(wsPoolProperties.getInbound().getQueueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(wsPoolProperties.getInbound().getAwaitTerminationSeconds());
        executor.initialize();
        return executor;
    }

    @Bean("websocketOutboundExecutor")
    public ThreadPoolTaskExecutor outboundChannelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ws-outbound-");
        executor.setCorePoolSize(wsPoolProperties.getOutbound().getCoreSize());
        executor.setMaxPoolSize(wsPoolProperties.getOutbound().getMaxSize());
        executor.setQueueCapacity(wsPoolProperties.getOutbound().getQueueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(wsPoolProperties.getOutbound().getAwaitTerminationSeconds());
        executor.initialize();
        return executor;
    }
}
