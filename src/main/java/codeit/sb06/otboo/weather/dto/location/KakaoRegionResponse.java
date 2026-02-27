package codeit.sb06.otboo.weather.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // meta 같은 건 전부 무시
public record KakaoRegionResponse(
    List<KakaoRegionDocument> documents
) {

}