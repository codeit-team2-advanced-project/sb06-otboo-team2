package codeit.sb06.otboo.weather.mapper;

import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.HumidityDto;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationDto;
import codeit.sb06.otboo.weather.dto.weather.TemperatureDto;
import codeit.sb06.otboo.weather.dto.weather.WeatherDto;
import codeit.sb06.otboo.weather.dto.weather.WindSpeedDto;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.model.SnapshotCandidate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {

  public Weather toSnapshot(
      SnapshotCandidate c,
      double latitude,
      double longitude
  ) {
    return Weather.builder()
        .date(c.date())
        .latitude(latitude)
        .longitude(longitude)
        .skyStatus(c.skyStatus())
        .precipitationType(c.precipitation().type())
        .precipitationAmount(c.precipitation().amount())
        .precipitationProbability(c.precipitation().probability())
        .tempCurrent(c.temperature().current())
        .tempMin(c.temperature().min())
        .tempMax(c.temperature().max())
        .humidity(c.humidity().current())
        .windSpeed(c.windSpeed().speed())
        .windStrength(c.windSpeed().asWord())
        .forecastAt(c.forecastAt())
        .createdAt(LocalDateTime.now())
        .build();
  }

  public WeatherDto toWeatherDto(Weather s, LocationDto location) {
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
