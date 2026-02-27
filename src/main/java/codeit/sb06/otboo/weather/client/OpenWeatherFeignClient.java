package codeit.sb06.otboo.weather.client;

import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openWeatherFeignClient", url = "https://api.openweathermap.org")
public interface OpenWeatherFeignClient {

  @GetMapping("/data/2.5/forecast")
  OpenWeatherForecastApiResponse fetchForecast(
      @RequestParam("lat") double latitude,
      @RequestParam("lon") double longitude,
      @RequestParam("appid") String apiKey,
      @RequestParam("units") String units
  );
}
