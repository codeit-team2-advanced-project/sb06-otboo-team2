package codeit.sb06.otboo.weather;

import codeit.sb06.otboo.weather.dto.weather.WeatherDto;
import codeit.sb06.otboo.weather.service.WeatherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class Weather {

  private final WeatherService weatherService;
  private Double latitude;
  private Double longitude;

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public List<WeatherDto> getWeather() throws Exception {
    if (latitude == null || longitude == null) {
      throw new IllegalStateException("latitude/longitude must be set before calling getWeather()");
    }
    return weatherService.getCurrentWeather(longitude, latitude);
  }
}
