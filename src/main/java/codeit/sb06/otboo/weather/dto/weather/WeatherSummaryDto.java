package codeit.sb06.otboo.weather.dto.weather;

import java.util.UUID;

public record WeatherSummaryDto(
    UUID weatherId,
    SkyStatus skyStatus,
    PrecipitationDto precipitation,
    TemperatureDto temperature
) {}
