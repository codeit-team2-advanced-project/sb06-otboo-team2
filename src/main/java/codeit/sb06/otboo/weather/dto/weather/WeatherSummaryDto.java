package codeit.sb06.otboo.weather.dto.weather;

import codeit.sb06.otboo.weather.entity.Weather;
import java.util.UUID;

public record WeatherSummaryDto(
    UUID weatherId,
    SkyStatus skyStatus,
    PrecipitationDto precipitation,
    TemperatureDto temperature
) {
  public static WeatherSummaryDto from(Weather s) {
    return new WeatherSummaryDto(
        s.getId(),
        s.getSkyStatus(),
        new PrecipitationDto(
            s.getPrecipitationType(),
            s.getPrecipitationAmount(),
            s.getPrecipitationProbability()
        ),
        new TemperatureDto(
            s.getTempCurrent(),
            0.0,
            s.getTempMin(),
            s.getTempMax()
        )
    );
  }
}
