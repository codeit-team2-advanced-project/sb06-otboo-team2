package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import codeit.sb06.otboo.weather.client.WeatherApiClient;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionDocument;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse.Metric;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WeatherDto;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.mapper.WeatherMapper;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import codeit.sb06.otboo.weather.service.WeatherService;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private WeatherApiClient weatherApiClient;

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

    OpenWeatherForecastApiResponse response = new OpenWeatherForecastApiResponse(List.of(
        new OpenWeatherForecastApiResponse.Item(
            dt1,
            new Metric(10.0, 9.0, 12.0, 60.0),
            List.of(new OpenWeatherForecastApiResponse.Weather("Clear")),
            new OpenWeatherForecastApiResponse.Wind(2.0),
            new OpenWeatherForecastApiResponse.Rain(1.2),
            null,
            0.1
        ),
        new OpenWeatherForecastApiResponse.Item(
            dt2,
            new Metric(8.0, 7.0, 11.0, 40.0),
            List.of(new OpenWeatherForecastApiResponse.Weather("Clear")),
            new OpenWeatherForecastApiResponse.Wind(4.0),
            null,
            null,
            0.3
        )
    ));

    LocationDto location = new LocationDto(
        lat, lon, 127.0, 37.5, List.of("서울특별시 중구")
    );

    given(weatherApiClient.fetchForecast(lat, lon))
        .willReturn(response);

    given(weatherApiClient.resolveLocationSafely(lon, lat))
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
    assertThat(daily.temperature().min()).isEqualTo(7.0);
    assertThat(daily.temperature().max()).isEqualTo(12.0);
    assertThat(daily.temperature().current()).isEqualTo(10.0);
    assertThat(daily.precipitation().type()).isEqualTo(PrecipitationType.RAIN);
    assertThat(daily.precipitation().amount()).isEqualTo(1.2);
    assertThat(daily.precipitation().probability()).isEqualTo(0.1);
    assertThat(daily.skyStatus()).isEqualTo(SkyStatus.CLEAR);
  }

  @Test
  void getLocation_클라이언트_결과를_반환한다() throws Exception {
    double lon = 126.9780;
    double lat = 37.5665;
    KakaoRegionResponse kakao = new KakaoRegionResponse(
        List.of(new KakaoRegionDocument("서울특별시 중구", "서울특별시", "중구", "명동", 126.9780, 37.5665))
    );
    LocationDto expected = new LocationDto(lat, lon, 126.9780, 37.5665, List.of("서울특별시", "중구", "명동"));

    given(weatherApiClient.fetchRegion(lon, lat)).willReturn(kakao);
    given(weatherApiClient.toLocationDto(lat, lon, kakao.documents())).willReturn(expected);

    LocationDto result = weatherService.getLocation(lon, lat);

    assertThat(result).isEqualTo(expected);
    verify(weatherApiClient).fetchRegion(lon, lat);
    verify(weatherApiClient).toLocationDto(lat, lon, kakao.documents());
  }

  @Test
  void getCurrentWeather_예보가_비어있으면_빈목록을_반환하고_저장하지_않는다() throws Exception {
    double lat = 37.5665;
    double lon = 126.9780;
    double normalizedLat = Math.round(lat * 100.0) / 100.0;
    double normalizedLon = Math.round(lon * 100.0) / 100.0;

    given(weatherApiClient.fetchForecast(lat, lon))
        .willReturn(new OpenWeatherForecastApiResponse(List.of()));
    given(weatherApiClient.resolveLocationSafely(lon, lat))
        .willReturn(new LocationDto(lat, lon, null, null, List.of()));
    given(weatherRepository.findByLatitudeAndLongitudeAndDateIn(
        eq(normalizedLat), eq(normalizedLon), anyList()
    )).willReturn(List.of());

    List<WeatherDto> result = weatherService.getCurrentWeather(lon, lat);

    assertThat(result).isEmpty();
    verify(weatherRepository, never()).saveAll(any());
  }

  @Test
  void getCurrentWeather_이미_스냅샷이_있으면_추가저장하지_않는다() throws Exception {
    double lat = 37.5665;
    double lon = 126.9780;
    double normalizedLat = Math.round(lat * 100.0) / 100.0;
    double normalizedLon = Math.round(lon * 100.0) / 100.0;

    long dt = LocalDate.of(2026, 1, 31)
        .atTime(12, 0)
        .atZone(ZoneId.of("Asia/Seoul"))
        .toEpochSecond();
    OpenWeatherForecastApiResponse response = new OpenWeatherForecastApiResponse(List.of(
        new OpenWeatherForecastApiResponse.Item(
            dt,
            new Metric(10.0, 9.0, 12.0, 60.0),
            List.of(new OpenWeatherForecastApiResponse.Weather("Clear")),
            new OpenWeatherForecastApiResponse.Wind(2.0),
            null,
            null,
            0.1
        )
    ));
    LocationDto location = new LocationDto(lat, lon, 127.0, 37.5, List.of("서울특별시 중구"));
    LocalDate forecastDate = LocalDate.of(2026, 1, 31);
    Weather existing = Weather.builder()
        .date(forecastDate)
        .forecastAt(LocalDateTime.of(2026, 1, 31, 12, 0))
        .latitude(normalizedLat)
        .longitude(normalizedLon)
        .skyStatus(SkyStatus.CLEAR)
        .precipitationType(PrecipitationType.NONE)
        .precipitationAmount(0.0)
        .precipitationProbability(0.1)
        .tempCurrent(10.0)
        .tempMin(9.0)
        .tempMax(12.0)
        .humidity(60.0)
        .windSpeed(2.0)
        .windStrength(codeit.sb06.otboo.weather.dto.weather.WindStrength.WEAK)
        .build();

    given(weatherApiClient.fetchForecast(lat, lon)).willReturn(response);
    given(weatherApiClient.resolveLocationSafely(lon, lat)).willReturn(location);
    given(weatherRepository.findByLatitudeAndLongitudeAndDateIn(
        eq(normalizedLat), eq(normalizedLon), anyList()
    )).willReturn(List.of(existing));

    List<WeatherDto> result = weatherService.getCurrentWeather(lon, lat);

    assertThat(result).hasSize(1);
    verify(weatherRepository, never()).saveAll(any());
  }
}
