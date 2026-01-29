package codeit.sb06.otboo.weather.client;

import codeit.sb06.otboo.weather.dto.location.KakaoRegionDocument;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.weather.LocationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoLocationClient {

  @Value("${kakao.key}")
  private String kakaoKey;

  private final ObjectMapper objectMapper;
  private final SimpleHttpClient httpClient;

  public KakaoRegionResponse fetchRegion(double longitude, double latitude) throws Exception {
    String url = buildCoord2RegionUrl(longitude, latitude);
    String raw = httpClient.get(url, Map.of("Authorization", "KakaoAK " + kakaoKey));
    return objectMapper.readValue(raw, KakaoRegionResponse.class);
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
        .filter(d -> "H".equals(d.regionType()))
        .findFirst()
        .orElse(docs.get(0));

    List<String> locationNames = docs.stream()
        .map(KakaoRegionDocument::addressName)
        .toList();

    return new LocationDto(
        latitude,
        longitude,
        picked.x(),
        picked.y(),
        locationNames
    );
  }

  private String buildCoord2RegionUrl(double longitude, double latitude) {
    return "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json"
        + "?x=" + longitude
        + "&y=" + latitude;
  }
}