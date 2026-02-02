package codeit.sb06.otboo.weather.entity;

import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weathers")
public class Weather {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private SkyStatus skyStatus;

  @Enumerated(EnumType.STRING)
  private PrecipitationType precipitationType;

  private double precipitationAmount;
  private double precipitationProbability;

  private double tempCurrent;
  private double tempMin;
  private double tempMax;
  private double humidity;
  private double windSpeed;

  @Enumerated(EnumType.STRING)
  private WindStrength windStrength;

  // --- cache / reuse keys ---
  private LocalDate date;
  private double latitude;
  private double longitude;

  private LocalDateTime forecastAt;
  private LocalDateTime createdAt;
}
