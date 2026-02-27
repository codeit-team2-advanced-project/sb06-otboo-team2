package codeit.sb06.otboo.weather.service;

import codeit.sb06.otboo.weather.client.WeatherApiClient;
import codeit.sb06.otboo.weather.dto.location.KakaoRegionResponse;
import codeit.sb06.otboo.weather.dto.location.LocationDto;
import codeit.sb06.otboo.weather.dto.weather.*;
import codeit.sb06.otboo.weather.dto.weather.OpenWeatherForecastApiResponse.Item;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.mapper.WeatherMapper;
import codeit.sb06.otboo.weather.model.SnapshotCandidate;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

  private static final ZoneId FORECAST_ZONE = ZoneId.of("Asia/Seoul");

  private final WeatherApiClient weatherApiClient;
  private final WeatherRepository weatherRepository;
  private final WeatherMapper weatherMapper;

  public List<WeatherDto> getCurrentWeather(double longitude, double latitude)
      throws Exception {
    double normalizedLatitude = round2(latitude);
    double normalizedLongitude = round2(longitude);
    OpenWeatherForecastApiResponse response = weatherApiClient.fetchForecast(latitude, longitude);
    LocationDto location = weatherApiClient.resolveLocationSafely(longitude, latitude);
    List<SnapshotCandidate> candidates = aggregateDaily(response, FORECAST_ZONE);

    List<LocalDate> dates = candidates.stream()
        .map(SnapshotCandidate::date)
        .flatMap(date -> Stream.of(date, date.minusDays(1)))
        .distinct()
        .toList();

    Map<LocalDate, Weather> existingByDate =
        findExistingByDate(normalizedLatitude, normalizedLongitude, dates);
    saveMissingSnapshots(candidates, existingByDate, normalizedLatitude, normalizedLongitude);

    return candidates.stream()
        .map(c -> existingByDate.get(c.date()))
        .filter(s -> s != null)
        .map(s -> toDtoWithDiff(s, existingByDate, location))
        .toList();
  }

  public LocationDto getLocation(double longitude, double latitude) throws Exception {
    KakaoRegionResponse kakao = weatherApiClient.fetchRegion(longitude, latitude);
    return weatherApiClient.toLocationDto(latitude, longitude, kakao.documents());
  }

  public List<SnapshotCandidate> aggregateDaily(
      OpenWeatherForecastApiResponse response,
      ZoneId zoneId
  ) {
    if (response == null || response.list() == null || response.list().isEmpty()) {
      return List.of();
    }

    Map<LocalDate, List<Item>> byDate =
        response.list().stream()
            .collect(Collectors.groupingBy(item ->
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond(item.dt()),
                    zoneId
                ).toLocalDate()
            ));

    return byDate.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(e -> toCandidate(e.getKey(), e.getValue(), zoneId))
        .toList();
  }

  private SnapshotCandidate toCandidate(
      LocalDate date,
      List<OpenWeatherForecastApiResponse.Item> items,
      ZoneId zoneId
  ) {
    if (items == null || items.isEmpty()) {
      return new SnapshotCandidate(
          date,
          date.atTime(12, 0),
          SkyStatus.CLEAR,
          new PrecipitationDto(PrecipitationType.NONE, 0.0, 0.0),
          new TemperatureDto(0.0, 0.0, 0.0, 0.0),
          new HumidityDto(0.0, 0.0),
          new WindSpeedDto(0.0, WindStrength.WEAK)
      );
    }

    double minTemp = items.stream().mapToDouble(i -> i.metric().tempMin()).min().orElse(0);
    double maxTemp = items.stream().mapToDouble(i -> i.metric().tempMax()).max().orElse(0);

    LocalDateTime target = date.atTime(12, 0);
    long targetEpoch = target.atZone(zoneId).toEpochSecond();

    Item closest = items.stream()
        .min((a, b) -> Long.compare(
            Math.abs(a.dt() - targetEpoch),
            Math.abs(b.dt() - targetEpoch)
        ))
        .orElse(items.get(0));

    double currentTemp = closest.metric().temp();
    double currentHumidity = closest.metric().humidity();
    double windSpeed = closest.wind() != null ? closest.wind().speed() : 0.0;
    double pop = closest.pop() != null ? closest.pop() : 0.0;

    double rainAmount = closest.rain() != null && closest.rain().amountFor3h() != null
        ? closest.rain().amountFor3h()
        : 0.0;
    double snowAmount = closest.snow() != null && closest.snow().amountFor3h() != null
        ? closest.snow().amountFor3h()
        : 0.0;

    PrecipitationType precipitationType =
        snowAmount > 0 ? PrecipitationType.SNOW :
            (rainAmount > 0 ? PrecipitationType.RAIN : PrecipitationType.NONE);

    String representativeMain = firstWeatherMain(closest.weather());
    if (representativeMain == null) {
      representativeMain = "N/A";
    }

    LocalDateTime forecastAt = target;

    return new SnapshotCandidate(
        date,
        forecastAt,
        mapSkyStatus(representativeMain),
        new PrecipitationDto(precipitationType, round2(rainAmount + snowAmount), round2(pop)),
        new TemperatureDto(round1(currentTemp), 0.0, round1(minTemp), round1(maxTemp)),
        new HumidityDto(round1(currentHumidity), 0.0),
        new WindSpeedDto(round1(windSpeed), windWord(windSpeed))
    );
  }

  private String firstWeatherMain(List<OpenWeatherForecastApiResponse.Weather> weatherList) {
    if (weatherList == null || weatherList.isEmpty() || weatherList.get(0) == null) return null;
    var w = weatherList.get(0);
    return w.condition();
  }

  private Map<LocalDate, Weather> findExistingByDate(
      double latitude,
      double longitude,
      List<LocalDate> dates
  ) {
    return weatherRepository
        .findByLatitudeAndLongitudeAndDateIn(latitude, longitude, dates)
        .stream()
        .collect(Collectors.toMap(Weather::getDate, Function.identity()));
  }

  private void saveMissingSnapshots(
      List<SnapshotCandidate> candidates,
      Map<LocalDate, Weather> existingByDate,
      double latitude,
      double longitude
  ) {
    List<Weather> toSave = candidates.stream()
        .filter(c -> !existingByDate.containsKey(c.date()))
        .map(c -> weatherMapper.toSnapshot(c, latitude, longitude))
        .toList();

    if (!toSave.isEmpty()) {
      for (Weather saved : weatherRepository.saveAll(toSave)) {
        existingByDate.put(saved.getDate(), saved);
      }
    }
  }

  private double round1(double v) {
    return Math.round(v * 10.0) / 10.0;
  }

  private double round2(double v) {
    return Math.round(v * 100.0) / 100.0;
  }

  private WeatherDto toDtoWithDiff(
      Weather current,
      Map<LocalDate, Weather> existingByDate,
      LocationDto location
  ) {
    Weather previous = existingByDate.get(current.getDate().minusDays(1));
    double humidityDiff = 0.0;
    double tempDiff = 0.0;
    if (previous != null) {
      humidityDiff = current.getHumidity() - previous.getHumidity();
      tempDiff = current.getTempCurrent() - previous.getTempCurrent();
    }

    return WeatherDto.from(
        current,
        location,
        round1(humidityDiff),
        round1(tempDiff)
    );
  }

  private SkyStatus mapSkyStatus(String condition) {
    return switch (condition) {
      case "Clear" -> SkyStatus.CLEAR;
      case "Clouds" -> SkyStatus.CLOUDY;
      case "Rain", "Snow" -> SkyStatus.MOSTLY_CLOUDY;
      default -> SkyStatus.CLEAR;
    };
  }

  private WindStrength windWord(double speed) {
    if (speed < 3) return WindStrength.WEAK;
    if (speed < 8) return WindStrength.MODERATE;
    return WindStrength.STRONG;
  }
}
