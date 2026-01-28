package codeit.sb06.otboo.weather.dto.weather;

public record WindSpeedDto(
    double speed,
    WindStrength asWord
) {}