package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import codeit.sb06.otboo.weather.client.KakaoLocationFeignClient;
import codeit.sb06.otboo.weather.client.OpenWeatherFeignClient;
import codeit.sb06.otboo.weather.client.WeatherApiClient;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionDocument;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherApiClientTest {

  @Mock
  private OpenWeatherFeignClient openWeatherFeignClient;

  @Mock
  private KakaoLocationFeignClient kakaoLocationFeignClient;

  private WeatherApiClient client;

  @BeforeEach
  void setUp() throws Exception {
    client = new WeatherApiClient(openWeatherFeignClient, kakaoLocationFeignClient);
    setPrivateField(client, "openApiKey", "test-openweather-key");
    setPrivateField(client, "kakaoKey", "test-kakao-key");
  }

  @Test
  void fetchForecast_정상적으로_호출한다() {
    double lat = 37.5665;
    double lon = 126.9780;
    OpenWeatherForecastApiResponse response = new OpenWeatherForecastApiResponse(
        List.of(
            new OpenWeatherForecastApiResponse.Item(
                1700000000L,
                new OpenWeatherForecastApiResponse.Metric(10.5, 9.0, 12.0, 60.0),
                List.of(new OpenWeatherForecastApiResponse.Weather("Clear")),
                new OpenWeatherForecastApiResponse.Wind(2.5),
                null,
                null,
                0.2
            )
        )
    );
    given(openWeatherFeignClient.fetchForecast(lat, lon, "test-openweather-key", "metric"))
        .willReturn(response);

    OpenWeatherForecastApiResponse result = client.fetchForecast(lat, lon);

    assertThat(result).isNotNull();
    assertThat(result.list()).hasSize(1);
    then(openWeatherFeignClient).should()
        .fetchForecast(lat, lon, "test-openweather-key", "metric");
  }

  @Test
  void resolveLocationSafely_정상적으로_위치정보를_만든다() {
    double lon = 126.9780;
    double lat = 37.5665;

    KakaoRegionResponse response = new KakaoRegionResponse(
        List.of(
            new KakaoRegionDocument("서울특별시 강서구", "서울특별시", "강서구", "방화2동", 126.9, 37.5)
        )
    );
    given(kakaoLocationFeignClient.fetchRegion("KakaoAK test-kakao-key", lon, lat))
        .willReturn(response);

    LocationDto dto = client.resolveLocationSafely(lon, lat);

    assertThat(dto.latitude()).isEqualTo(lat);
    assertThat(dto.longitude()).isEqualTo(lon);
    assertThat(dto.locationNames()).containsExactly("서울특별시", "강서구", "방화2동");
  }

  @Test
  void resolveLocationSafely_실패시_fallback한다() {
    double lon = 126.9780;
    double lat = 37.5665;
    given(kakaoLocationFeignClient.fetchRegion("KakaoAK test-kakao-key", lon, lat))
        .willThrow(new RuntimeException("Kakao API down"));

    LocationDto dto = client.resolveLocationSafely(lon, lat);

    assertThat(dto.latitude()).isEqualTo(lat);
    assertThat(dto.longitude()).isEqualTo(lon);
    assertThat(dto.x()).isNull();
    assertThat(dto.y()).isNull();
    assertThat(dto.locationNames()).isEmpty();
  }

  private static void setPrivateField(Object target, String fieldName, Object value)
      throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }
}
