package codeit.sb06.otboo.weather.dto.weather;

public record TemperatureDto(
    double current,
    double comparedToDayBefore,
    double min,
    double max
) {}