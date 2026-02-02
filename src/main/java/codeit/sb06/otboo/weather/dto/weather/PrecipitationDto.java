package codeit.sb06.otboo.weather.dto.weather;


public record PrecipitationDto(
    PrecipitationType type,
    double amount,
    double probability
) {}