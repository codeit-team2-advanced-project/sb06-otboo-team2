package codeit.sb06.otboo.weather.dto.weather;

import java.time.Instant;
import java.util.UUID;

public record WeatherResponseDto(
    UUID id,
    Instant forecastedAt,
    Instant forecastAt,
    LocationDto location,
    SkyStatus skyStatus,
    PrecipitationDto precipitation,
    HumidityDto humidity,
    TemperatureDto temperature,
    WindSpeedDto windSpeed
) {}