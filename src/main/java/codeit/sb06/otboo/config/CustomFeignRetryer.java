package codeit.sb06.otboo.config;

import feign.Request.HttpMethod;
import feign.RetryableException;
import feign.Retryer;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignRetryer implements Retryer {

  private static final Set<HttpMethod> RETRYABLE_METHODS = Set.of(
      HttpMethod.GET,
      HttpMethod.HEAD,
      HttpMethod.OPTIONS
  );

  private final long period;
  private final long maxPeriod;
  private final int maxAttempts;
  private int attempt;
  private long nextBackoff;

  public CustomFeignRetryer(long period, long maxPeriod, int maxAttempts) {
    this.period = period;
    this.maxPeriod = maxPeriod;
    this.maxAttempts = maxAttempts;
    this.attempt = 1;
    this.nextBackoff = period;
  }

  @Override
  public void continueOrPropagate(RetryableException e) {
    HttpMethod method = e.method();
    if (method != null && !RETRYABLE_METHODS.contains(method)) {
      throw e;
    }

    if (attempt++ >= maxAttempts) {
      throw e;
    }

    long sleepFor = Math.min(nextBackoff, maxPeriod);
    nextBackoff = Math.min(nextBackoff * 2, maxPeriod);

    log.warn("Feign retry attempt={} method={} status={} waitMs={}",
        attempt - 1, method, e.status(), sleepFor);

    try {
      Thread.sleep(sleepFor);
    } catch (InterruptedException interrupted) {
      Thread.currentThread().interrupt();
      throw e;
    }
  }

  @Override
  public Retryer clone() {
    return new CustomFeignRetryer(period, maxPeriod, maxAttempts);
  }
}
