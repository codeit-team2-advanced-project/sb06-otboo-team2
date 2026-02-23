package codeit.sb06.otboo.weather.client;

import codeit.sb06.otboo.weather.dto.location.KakaoRegionDocument;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherApiClient {

  @Value("${openweather.key}")
  private String openApiKey;

  @Value("${kakao.key}")
  private String kakaoKey;

  private final OpenWeatherFeignClient openWeatherFeignClient;
  private final KakaoLocationFeignClient kakaoLocationFeignClient;

  public OpenWeatherForecastApiResponse fetchForecast(double latitude, double longitude) {
    return openWeatherFeignClient.fetchForecast(latitude, longitude, openApiKey, "metric");
  }

  public KakaoRegionResponse fetchRegion(double longitude, double latitude) {
    return kakaoLocationFeignClient.fetchRegion(
        "KakaoAK " + kakaoKey,
        longitude,
        latitude
    );
  }

  public LocationDto resolveLocationSafely(double longitude, double latitude) {
    try {
      KakaoRegionResponse kakao = fetchRegion(longitude, latitude);
      return toLocationDto(latitude, longitude, kakao.documents());
    } catch (Exception e) {
      return new LocationDto(latitude, longitude, null, null, List.of());
    }
  }

  public LocationDto toLocationDto(double latitude, double longitude, List<KakaoRegionDocument> docs) {
    if (docs == null || docs.isEmpty()) {
      return new LocationDto(latitude, longitude, null, null, List.of());
    }

    KakaoRegionDocument picked = docs.stream()
        .findFirst()
        .orElse(docs.get(0));

    List<String> locationNames = List.of(
        picked.region1DepthName(),
        picked.region2DepthName(),
        picked.region3DepthName()
    );

    return new LocationDto(
        latitude,
        longitude,
        picked.x(),
        picked.y(),
        locationNames
    );
  }
}
