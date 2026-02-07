package codeit.sb06.otboo.exception.weather;

import codeit.sb06.otboo.exception.RootException;
import java.util.UUID;

public class WeatherException extends RootException {

  public WeatherException(String message, int status) {
    super(message, status);
  }

  public WeatherException(String message, Throwable cause, int status) {
    super(message, cause, status);
  }

  public static WeatherException weatherNotFound(UUID weatherId) {
    WeatherException ex = new WeatherException("WEATHER_NOT_FOUND", 404);
    ex.addDetail("weatherId", weatherId);
    return ex;
  }
}
