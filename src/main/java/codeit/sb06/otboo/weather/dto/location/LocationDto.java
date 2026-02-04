package codeit.sb06.otboo.weather.dto.location;

import java.util.List;

public record LocationDto(
    double latitude,
    double longitude,
    Double x,
    Double y,
    List<String> locationNames
) {}
