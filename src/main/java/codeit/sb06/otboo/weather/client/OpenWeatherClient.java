package codeit.sb06.otboo.weather.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenWeatherClient {

  @Value("${openweather.key}")
  private String openApiKey;

  private final ObjectMapper objectMapper;
  private final SimpleHttpClient httpClient;

  public OpenWeatherForecastResponse fetchForecast(double latitude, double longitude)
      throws Exception {
    String url =
        "https://api.openweathermap.org/data/2.5/forecast"
            + "?lat=" + latitude
            + "&lon=" + longitude
            + "&appid=" + openApiKey
            + "&units=metric";

    String raw = httpClient.get(url, Map.of("Accept", "application/json"));
    return objectMapper.readValue(raw, OpenWeatherForecastResponse.class);
  }
}
