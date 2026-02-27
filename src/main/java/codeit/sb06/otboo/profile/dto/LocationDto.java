package codeit.sb06.otboo.profile.dto;

import codeit.sb06.otboo.profile.entity.Location;
import java.util.List;

public record LocationDto(
    double latitude,
    double longitude,
    int x,
    int y,
    List<String> locationNames
) {

    public static LocationDto from(Location location) {
        return new LocationDto(
            location.getLatitude(),
            location.getLongitude(),
            location.getX(),
            location.getY(),
            location.getLocationDetails()
        );
    }
}
