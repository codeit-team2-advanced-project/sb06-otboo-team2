package codeit.sb06.otboo.config;

import feign.Response;
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
    String message = "[" + methodKey + "] status=" + status + ", body=" + body;

    return switch (status) {
      case 400 -> new BadRequestException(message);
      case 404 -> new NotFoundException(message);
      case 429 -> new TooManyRequestsException(message);
      case 500, 502, 503, 504 -> new ServerErrorException(message);
      default -> defaultErrorDecoder.decode(methodKey, response);
    };
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
