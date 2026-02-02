package codeit.sb06.otboo.weather.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherSummaryDto(
    List<Item> list
) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Item(
      long dt,
      Main main,
      @JsonProperty("weather")
      List<Weather> weather,
      Wind wind,
      Rain rain,
      Snow snow,
      Double pop
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Main(
      @JsonProperty("temp")
      double temp,
      @JsonProperty("temp_min")
      double tempMin,
      @JsonProperty("temp_max")
      double tempMax,
      @JsonProperty("humidity")
      double humidity
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Weather(
      String main
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Wind(
      @JsonProperty("speed")
      double speed
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Rain(
      @JsonProperty("3h")
      Double volume3h
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Snow(
      @JsonProperty("3h")
      Double volume3h
  ) {}
}
