package codeit.sb06.otboo.weather.dto.weather;

public record HumidityDto(
    double current,
    double comparedToDayBefore
) {}