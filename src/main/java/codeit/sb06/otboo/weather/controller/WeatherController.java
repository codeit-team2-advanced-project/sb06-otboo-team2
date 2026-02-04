package codeit.sb06.otboo.weather.controller;

import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.WeatherDto;
import codeit.sb06.otboo.weather.service.WeatherService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/weathers")
@RequiredArgsConstructor
public class WeatherController {

  private final WeatherService weatherService;

  @GetMapping
  public ResponseEntity<List<WeatherDto>> getCurrentWeather(
      @RequestParam double longitude,
      @RequestParam double latitude
  ) throws Exception {
    validate(longitude, latitude);
    return ResponseEntity.ok(weatherService.getCurrentWeather(longitude, latitude));
  }

  @GetMapping("/location")
  public ResponseEntity<LocationDto> getRegionByCoordinate(
      @RequestParam double longitude,
      @RequestParam double latitude
  ) throws IOException {
    validate(longitude, latitude);
    try {
      return ResponseEntity.ok(weatherService.getLocation(longitude, latitude));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void validate(double lon, double lat) {
    if (lat < -90 || lat > 90) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude must be between -90 and 90");
    }
    if (lon < -180 || lon > 180) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "longitude must be between -180 and 180");
    }
  }
}
