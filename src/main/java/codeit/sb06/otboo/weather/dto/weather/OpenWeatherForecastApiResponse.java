package codeit.sb06.otboo.weather.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherForecastApiResponse(
    List<Item> list
) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Item(
      long dt,
      @JsonProperty("main")
      Metric metric,
      @JsonProperty("weather")
      List<Weather> weather,
      Wind wind,
      Rain rain,
      Snow snow,
      Double pop
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Metric(
      double temp,
      @JsonProperty("temp_min")
      double tempMin,
      @JsonProperty("temp_max")
      double tempMax,
      double humidity
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Weather(
      @JsonProperty("main")
      String condition
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Wind(
      double speed
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Rain(
      @JsonProperty("3h")
      Double amountFor3h
  ) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Snow(
      @JsonProperty("3h")
      Double amountFor3h
  ) {}
}
