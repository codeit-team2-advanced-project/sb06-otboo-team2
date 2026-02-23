package codeit.sb06.otboo.weather.client;

import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoLocationFeignClient", url = "https://dapi.kakao.com")
public interface KakaoLocationFeignClient {

  @GetMapping("/v2/local/geo/coord2regioncode.json")
  KakaoRegionResponse fetchRegion(
      @RequestHeader("Authorization") String authorization,
      @RequestParam("x") double longitude,
      @RequestParam("y") double latitude
  );
}
