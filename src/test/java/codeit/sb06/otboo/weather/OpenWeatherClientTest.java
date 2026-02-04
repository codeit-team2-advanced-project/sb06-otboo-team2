package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import codeit.sb06.otboo.weather.client.OpenWeatherClient;
import codeit.sb06.otboo.weather.client.SimpleHttpClient;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenWeatherClientTest {

  @Mock
  private SimpleHttpClient httpClient;

  private ObjectMapper objectMapper;
  private OpenWeatherClient client;

  @BeforeEach
  void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    client = new OpenWeatherClient(objectMapper, httpClient);

    // @Value로 주입되는 openApiKey를 테스트에서 강제로 세팅
    setPrivateField(client, "openApiKey", "test-openweather-key");
  }

  @Test
  void fetchForecast_정상적으로_호출하고_JSON을_파싱한다() throws Exception {
    // given
    double lat = 37.5665;
    double lon = 126.9780;

    String expectedUrl =
        "https://api.openweathermap.org/data/2.5/forecast"
            + "?lat=" + lat
            + "&lon=" + lon
            + "&appid=test-openweather-key"
            + "&units=metric";

    String json = """
        {
          "list": [
            {
              "dt": 1700000000,
              "main": { "temp": 10.5, "temp_min": 9.0, "temp_max": 12.0, "humidity": 60 },
              "weather": [ { "main": "Clear", "icon": "01d" } ],
              "wind": { "speed": 2.5 },
              "pop": 0.2
            }
          ]
        }
        """;

    given(httpClient.get(eq(expectedUrl), anyMap())).willReturn(json);

    // when
    OpenWeatherForecastApiResponse result = client.fetchForecast(lat, lon);

    // then
    assertThat(result).isNotNull();
    assertThat(result.list()).hasSize(1);
    assertThat(result.list().get(0).metric().temp()).isEqualTo(10.5);
    assertThat(result.list().get(0).weather().get(0).condition()).isEqualTo("Clear");

    // 헤더 검증 (Accept 정확히 붙는지)
    then(httpClient).should().get(
        eq(expectedUrl),
        eq(Map.of("Accept", "application/json"))
    );
  }

  // --- test helper ---
  private static void setPrivateField(Object target, String fieldName, Object value)
      throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }
}
