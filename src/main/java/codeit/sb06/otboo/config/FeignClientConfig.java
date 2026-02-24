package codeit.sb06.otboo.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "codeit.sb06.otboo.weather.client")
public class FeignClientConfig {

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public Retryer feignRetryer() {
    // POST/PUT과 같은 상태 변경 요청은 멱등성이 보장될 때만 재시도해야 한다.
    return new Retryer.Default(200, 1000, 3);
  }

  @Bean
  public ErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
  }
}
