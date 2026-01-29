package codeit.sb06.otboo.weather;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import codeit.sb06.otboo.weather.client.KakaoLocationClient;
import codeit.sb06.otboo.weather.client.OpenWeatherClient;
import codeit.sb06.otboo.weather.dto.weather.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WeatherResponseDto;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

  @Mock
  private OpenWeatherClient openWeatherClient;

  @Mock
  private KakaoLocationClient kakaoLocationClient;

  @InjectMocks
  private WeatherService weatherService;

  @Test
  void 현재_날씨_정상_조회() throws Exception {
    // given
    double lat = 37.5665;
    double lon = 126.9780;

    LocationDto location = new LocationDto(
        lat, lon, 127.0, 37.5, List.of("서울특별시 중구")
    );

    String openWeatherJson = """
      {
        "main": {
          "temp": 10.5,
          "temp_min": 9.0,
          "temp_max": 12.0,
          "humidity": 60
        },
        "weather": [
          { "main": "Clear" }
        ],
        "wind": { "speed": 2.5 },
        "dt": 1700000000
      }
    """;

    JsonNode openWeatherNode = new ObjectMapper().readTree(openWeatherJson);

    given(kakaoLocationClient.resolveLocationSafely(lon, lat))
        .willReturn(location);

    given(openWeatherClient.fetchCurrentWeatherJson(lat, lon))
        .willReturn(openWeatherNode);

    // when
    WeatherResponseDto result =
        weatherService.getCurrentWeather(lon, lat);

    // then
    assertThat(result.location()).isEqualTo(location);
    assertThat(result.temperature().current()).isEqualTo(10.5);
    assertThat(result.humidity().current()).isEqualTo(60);
    assertThat(result.skyStatus()).isEqualTo(SkyStatus.CLEAR);
    assertThat(result.precipitation().type()).isEqualTo(PrecipitationType.NONE);
    assertThat(result.precipitation().amount()).isEqualTo(0.0);
    assertThat(result.windSpeed().asWord()).isEqualTo(WindStrength.WEAK);
    assertThat(result.forecastAt().getEpochSecond()).isEqualTo(1700000000L);
  }
}
