package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import codeit.sb06.otboo.weather.client.OpenWeatherClient;
import codeit.sb06.otboo.weather.client.SimpleHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
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
  void fetchCurrentWeatherJson_정상적으로_호출하고_JSON을_파싱한다() throws Exception {
    // given
    double lat = 37.5665;
    double lon = 126.9780;

    String expectedUrl =
        "https://api.openweathermap.org/data/2.5/weather"
            + "?lat=" + lat
            + "&lon=" + lon
            + "&appid=test-openweather-key"
            + "&units=metric";

    String json = """
        {
          "main": { "temp": 10.5 },
          "weather": [ { "main": "Clear" } ]
        }
        """;

    given(httpClient.get(eq(expectedUrl), anyMap())).willReturn(json);

    // when
    JsonNode result = client.fetchCurrentWeatherJson(lat, lon);

    // then
    assertThat(result).isNotNull();
    assertThat(result.get("main").get("temp").asDouble()).isEqualTo(10.5);
    assertThat(result.get("weather").get(0).get("main").asText()).isEqualTo("Clear");

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
