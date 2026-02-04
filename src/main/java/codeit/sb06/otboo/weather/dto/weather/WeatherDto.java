package codeit.sb06.otboo.weather.dto.weather;

import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.entity.Weather;
import java.time.LocalDateTime;
import java.util.UUID;

public record WeatherDto(
    UUID id,
    LocalDateTime forecastedAt,
    LocalDateTime forecastAt,
    LocationDto location,
    SkyStatus skyStatus,
    PrecipitationDto precipitation,
    HumidityDto humidity,
    TemperatureDto temperature,
    WindSpeedDto windSpeed
) {
  public static WeatherDto from(Weather s, LocationDto location) {
    return new WeatherDto(
        s.getId(),
        s.getCreatedAt() != null ? s.getCreatedAt() : LocalDateTime.now(),
        s.getForecastAt(),
        location,
        s.getSkyStatus(),
        new PrecipitationDto(
            s.getPrecipitationType(),
            s.getPrecipitationAmount(),
            s.getPrecipitationProbability()
        ),
        new HumidityDto(s.getHumidity(), 0.0),
        new TemperatureDto(
            s.getTempCurrent(),
            0.0,
            s.getTempMin(),
            s.getTempMax()
        ),
        new WindSpeedDto(s.getWindSpeed(), s.getWindStrength())
    );
  }
}
