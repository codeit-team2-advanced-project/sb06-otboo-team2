package codeit.sb06.otboo.config;

import feign.Request.HttpMethod;
import feign.Response;
import feign.RetryableException;
import feign.Util;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FeignErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    int status = response.status();
    String body = readBody(response);
    HttpMethod method = response.request() != null ? response.request().httpMethod() : null;
    String message = "[" + methodKey + "] status=" + status + ", body=" + body;

    if (isRetryable(method, status)) {
      return new RetryableException(status, message, method, (Long) null, response.request());
    }

    return switch (status) {
      case 400 -> new BadRequestException(message);
      case 404 -> new NotFoundException(message);
      case 429 -> new TooManyRequestsException(message);
      case 500, 502, 503, 504 -> new ServerErrorException(message);
      default -> defaultErrorDecoder.decode(methodKey, response);
    };
  }

  private boolean isRetryable(HttpMethod method, int status) {
    if (method == null) {
      return false;
    }

    boolean isSafeMethod = method == HttpMethod.GET
        || method == HttpMethod.HEAD
        || method == HttpMethod.OPTIONS;

    if (!isSafeMethod) {
      return false;
    }

    return status == 408 || status == 429 || status == 500 || status == 502
        || status == 503 || status == 504;
  }

  private String readBody(Response response) {
    if (response.body() == null) {
      return "";
    }
    try {
      return Util.toString(response.body().asReader(StandardCharsets.UTF_8));
    } catch (IOException e) {
      return "<failed to read response body>";
    }
  }

  public static class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
      super(message);
    }
  }

  public static class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
      super(message);
    }
  }

  public static class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
      super(message);
    }
  }

  public static class ServerErrorException extends RuntimeException {

    public ServerErrorException(String message) {
      super(message);
    }
  }
}
