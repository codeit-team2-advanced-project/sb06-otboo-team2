package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.assertThat;

import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import codeit.sb06.otboo.config.QueryDslConfig;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
class WeatherRepositoryTest {

  @Autowired
  private WeatherRepository weatherRepository;

  @Test
  void 날짜와_좌표로_저장하고_조회한다() {
    // given
    LocalDate date = LocalDate.of(2026, 1, 31);
    double lat = 37.5665;
    double lon = 126.9780;

    Weather weather = Weather.builder()
        .date(date)
        .latitude(lat)
        .longitude(lon)
        .skyStatus(SkyStatus.CLEAR)
        .precipitationType(PrecipitationType.NONE)
        .precipitationAmount(0.0)
        .precipitationProbability(0.1)
        .tempCurrent(1.0)
        .tempMin(-2.0)
        .tempMax(3.0)
        .humidity(55.0)
        .windSpeed(2.0)
        .windStrength(WindStrength.WEAK)
        .forecastAt(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .build();

    weatherRepository.save(weather);

    // when
    Weather result = weatherRepository
        .findByDateAndLatitudeAndLongitude(date, lat, lon)
        .orElseThrow();

    // then
    assertThat(result.getDate()).isEqualTo(date);
    assertThat(result.getLatitude()).isEqualTo(lat);
    assertThat(result.getLongitude()).isEqualTo(lon);
    assertThat(result.getSkyStatus()).isEqualTo(SkyStatus.CLEAR);
  }
}
