package codeit.sb06.otboo.weather.dto.weather;

import codeit.sb06.otboo.weather.dto.location.LocationDto;
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
) {}
