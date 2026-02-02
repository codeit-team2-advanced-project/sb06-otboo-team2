package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import codeit.sb06.otboo.weather.client.KakaoLocationClient;
import codeit.sb06.otboo.weather.client.OpenWeatherClient;
import codeit.sb06.otboo.weather.dto.weather.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WeatherDto;
import codeit.sb06.otboo.weather.dto.weather.WeatherSummaryDto;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.mapper.WeatherMapper;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import codeit.sb06.otboo.weather.service.WeatherService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

  @Mock
  private OpenWeatherClient openWeatherClient;

  @Mock
  private KakaoLocationClient kakaoLocationClient;

  @Mock
  private WeatherRepository weatherRepository;

  @Spy
  private WeatherMapper weatherMapper = new WeatherMapper();

  @InjectMocks
  private WeatherService weatherService;

  @Test
  void 일별_예보를_날짜별로_집계한다() throws Exception {
    // given
    double lat = 37.5665;
    double lon = 126.9780;
    double normalizedLat = Math.round(lat * 100.0) / 100.0;
    double normalizedLon = Math.round(lon * 100.0) / 100.0;

    long dt1 = LocalDate.of(2026, 1, 31)
        .atTime(12, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toEpochSecond();
    long dt2 = dt1 + 3 * 3600;

    WeatherSummaryDto response = new WeatherSummaryDto(List.of(
        new WeatherSummaryDto.Item(
            dt1,
            new WeatherSummaryDto.Main(10.0, 9.0, 12.0, 60.0),
            List.of(new WeatherSummaryDto.Weather("Clear")),
            new WeatherSummaryDto.Wind(2.0),
            new WeatherSummaryDto.Rain(1.2),
            null,
            0.1
        ),
        new WeatherSummaryDto.Item(
            dt2,
            new WeatherSummaryDto.Main(8.0, 7.0, 11.0, 40.0),
            List.of(new WeatherSummaryDto.Weather("Clear")),
            new WeatherSummaryDto.Wind(4.0),
            null,
            null,
            0.3
        )
    ));

    LocationDto location = new LocationDto(
        lat, lon, 127.0, 37.5, List.of("서울특별시 중구")
    );

    given(openWeatherClient.fetchForecast(lat, lon))
        .willReturn(response);

    given(kakaoLocationClient.resolveLocationSafely(lon, lat))
        .willReturn(location);

    given(weatherRepository.findByLatitudeAndLongitudeAndDateIn(
        eq(normalizedLat), eq(normalizedLon), anyList()
    )).willReturn(List.of());

    given(weatherRepository.saveAll(anyList()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    List<WeatherDto> result =
        weatherService.getCurrentWeather(lon, lat);

    // then
    assertThat(result).hasSize(1);
    WeatherDto daily = result.get(0);
    assertThat(daily.location()).isEqualTo(location);
    assertThat(daily.temperature().min()).isEqualTo(7.0);
    assertThat(daily.temperature().max()).isEqualTo(12.0);
    assertThat(daily.temperature().current()).isEqualTo(10.0);
    assertThat(daily.humidity().current()).isEqualTo(60.0);
    assertThat(daily.precipitation().type()).isEqualTo(PrecipitationType.RAIN);
    assertThat(daily.precipitation().amount()).isEqualTo(1.2);
    assertThat(daily.precipitation().probability()).isEqualTo(0.1);
    assertThat(daily.skyStatus()).isEqualTo(SkyStatus.CLEAR);
    assertThat(daily.windSpeed().asWord()).isEqualTo(WindStrength.WEAK);
  }
}
