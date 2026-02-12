package codeit.sb06.otboo.exception.weather;

import java.util.UUID;

public class WeatherNotFoundException extends WeatherException {

  private static final String DEFAULT_MESSAGE = "Weather not found";

  public WeatherNotFoundException(UUID weatherId) {
    super(DEFAULT_MESSAGE, 404);
    addDetail("weatherId", weatherId);
  }

  public WeatherNotFoundException() {
    super(DEFAULT_MESSAGE, 404);
  }

  public WeatherNotFoundException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause, 404);
  }
}
