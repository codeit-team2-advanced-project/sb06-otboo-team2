package codeit.sb06.otboo.weather.model;

import codeit.sb06.otboo.weather.dto.weather.HumidityDto;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationDto;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.TemperatureDto;
import codeit.sb06.otboo.weather.dto.weather.WindSpeedDto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SnapshotCandidate(
    LocalDate date,
    LocalDateTime forecastAt,
    SkyStatus skyStatus,
    PrecipitationDto precipitation,
    TemperatureDto temperature,
    HumidityDto humidity,
    WindSpeedDto windSpeed
) {}
