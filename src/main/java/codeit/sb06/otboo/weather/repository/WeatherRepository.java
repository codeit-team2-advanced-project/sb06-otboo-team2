package codeit.sb06.otboo.weather.repository;

import codeit.sb06.otboo.weather.entity.Weather;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, UUID> {

  Optional<Weather> findByDateAndLatitudeAndLongitude(
      LocalDate date,
      double latitude,
      double longitude
  );

  List<Weather> findByLatitudeAndLongitudeAndDateIn(
      double latitude,
      double longitude,
      Collection<LocalDate> dates
  );
}
