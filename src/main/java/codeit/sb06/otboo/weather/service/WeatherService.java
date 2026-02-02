package codeit.sb06.otboo.weather.service;

import codeit.sb06.otboo.weather.client.KakaoLocationClient;
import codeit.sb06.otboo.weather.client.OpenWeatherClient;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.weather.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

  private final OpenWeatherClient openWeatherClient;
  private final KakaoLocationClient kakaoLocationClient;

  public WeatherResponseDto getCurrentWeather(double longitude, double latitude) throws Exception {
    JsonNode root = openWeatherClient.fetchCurrentWeatherJson(latitude, longitude);

    JsonNode main = root.get("main");
    JsonNode weather = root.get("weather").get(0);
    JsonNode wind = root.get("wind");
    JsonNode rain = root.path("rain");

    double temp = main.get("temp").asDouble();
    double tmin = main.get("temp_min").asDouble();
    double tmax = main.get("temp_max").asDouble();
    double humidity = main.get("humidity").asDouble();
    double windSpeed = wind.get("speed").asDouble();
    double rainAmount = rain.path("1h").asDouble(0.0);

    String weatherMain = weather.get("main").asText(); // Clear, Rain, Clouds
    long dt = root.get("dt").asLong();

    Instant forecastAt = Instant.ofEpochSecond(dt);
    Instant now = Instant.now();

    LocationDto location = kakaoLocationClient.resolveLocationSafely(longitude, latitude);

    return new WeatherResponseDto(
        UUID.randomUUID(),
        now,
        forecastAt,
        location,
        mapSkyStatus(weatherMain),
        new PrecipitationDto(
            rainAmount > 0 ? PrecipitationType.RAIN : PrecipitationType.NONE,
            rainAmount,
            rainAmount > 0 ? 1.0 : 0.0
        ),
        new HumidityDto(humidity, 0.0),
        new TemperatureDto(temp, 0.0, tmin, tmax),
        new WindSpeedDto(windSpeed, windWord(windSpeed))
    );
  }

  public LocationDto getLocation(double longitude, double latitude) throws Exception {
    KakaoRegionResponse kakao = kakaoLocationClient.fetchRegion(longitude, latitude);
    return kakaoLocationClient.toLocationDto(latitude, longitude, kakao.documents());
  }

  private SkyStatus mapSkyStatus(String main) {
    return switch (main) {
      case "Clear" -> SkyStatus.CLEAR;
      case "Clouds" -> SkyStatus.CLOUDY;
      case "Rain", "Snow" -> SkyStatus.MOSTLY_CLOUDY;
      default -> SkyStatus.CLEAR;
    };
  }

  private WindStrength windWord(double speed) {
    if (speed < 3) return WindStrength.WEAK;
    if (speed < 8) return WindStrength.MODERATE;
    return WindStrength.STRONG;
  }
}