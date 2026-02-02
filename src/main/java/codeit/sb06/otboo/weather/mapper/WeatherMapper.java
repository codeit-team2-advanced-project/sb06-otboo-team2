package codeit.sb06.otboo.weather.mapper;

import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.model.SnapshotCandidate;
import java.time.Instant;
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
        .createdAt(Instant.now())
        .build();
  }
}
