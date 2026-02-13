package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import codeit.sb06.otboo.weather.client.KakaoLocationClient;
import codeit.sb06.otboo.weather.client.SimpleHttpClient;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.location.LocationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KakaoLocationClientTest {

  @Mock
  private SimpleHttpClient httpClient;

  private ObjectMapper objectMapper;
  private KakaoLocationClient client;

  @BeforeEach
  void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    client = new KakaoLocationClient(objectMapper, httpClient);

    // @Value로 주입되는 kakaoKey를 테스트에서 강제로 세팅
    setPrivateField(client, "kakaoKey", "test-kakao-key");
  }

  @Test
  void fetchRegion_정상적으로_호출하고_JSON을_파싱한다() throws Exception {
    // given
    double lon = 126.9780;
    double lat = 37.5665;

    String expectedUrl =
        "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=" + lon + "&y=" + lat;

    String json = """
        {
          "documents": [
            {
              "region_type": "H",
              "address_name": "서울특별시 중구",
              "x": 126.9780,
              "y": 37.5665
            }
          ]
        }
        """;

    given(httpClient.get(eq(expectedUrl), anyMap())).willReturn(json);

    // when
    KakaoRegionResponse result = client.fetchRegion(lon, lat);

    // then
    assertThat(result).isNotNull();
    assertThat(result.documents()).hasSize(1);
    assertThat(result.documents().get(0).addressName()).isEqualTo("서울특별시 중구");

    // 헤더 검증 (Authorization 정확히 붙는지)
    then(httpClient).should().get(
        eq(expectedUrl),
        eq(Map.of("Authorization", "KakaoAK test-kakao-key"))
    );
  }

  @Test
  void resolveLocationSafely_fetchRegion_성공시_LocationDto를_만든다() throws Exception {
    // given
    double lon = 126.9780;
    double lat = 37.5665;

    String json = """
        {
          "documents": [
            {
              "address_name": "서울특별시 강서구",
              "region_1depth_name": "서울특별시",
              "region_2depth_name": "강서구",
              "region_3depth_name": "방화2동",
              "x": 126.9,
              "y": 37.5
            },
            {
              "address_name": "인천광역시 서구 청라3동",
              "region_1depth_name": "인천광역시",
              "region_2depth_name": "서구",
              "region_3depth_name": "청라3동",
              "x": 127.1,
              "y": 37.6
            }
          ]
        }
        """;

    String expectedUrl =
        "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=" + lon + "&y=" + lat;

    given(httpClient.get(eq(expectedUrl), anyMap())).willReturn(json);

    // when
    LocationDto dto = client.resolveLocationSafely(lon, lat);

    // then
    assertThat(dto.latitude()).isEqualTo(lat);
    assertThat(dto.longitude()).isEqualTo(lon);

    // 첫 번째 doc의 x,y가 들어가야 함
    assertThat(dto.x()).isEqualTo(126.9);
    assertThat(dto.y()).isEqualTo(37.5);

    // locationNames는 첫 번째 doc의 depth name 목록
    assertThat(dto.locationNames())
        .containsExactly("서울특별시", "강서구", "방화2동");
  }

  @Test
  void resolveLocationSafely_fetchRegion_실패시_빈_LocationDto로_fallback한다() throws Exception {
    // given
    double lon = 126.9780;
    double lat = 37.5665;

    String expectedUrl =
        "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=" + lon + "&y=" + lat;

    given(httpClient.get(eq(expectedUrl), anyMap()))
        .willThrow(new RuntimeException("Kakao API down"));

    // when
    LocationDto dto = client.resolveLocationSafely(lon, lat);

    // then
    assertThat(dto.latitude()).isEqualTo(lat);
    assertThat(dto.longitude()).isEqualTo(lon);
    assertThat(dto.x()).isNull();
    assertThat(dto.y()).isNull();
    assertThat(dto.locationNames()).isEmpty();
  }

  // --- test helper ---
  private static void setPrivateField(Object target, String fieldName, Object value)
      throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }
}
