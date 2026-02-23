package codeit.sb06.otboo.clothes.service;

import codeit.sb06.otboo.clothes.dto.RecommendationDto;
import codeit.sb06.otboo.clothes.dto.RecommendedClothesDto;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesAttribute;
import codeit.sb06.otboo.clothes.entity.ClothesAttributeDef;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.profile.entity.Profile;
import codeit.sb06.otboo.profile.repository.ProfileRepository;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("getRecommendation: 옷이 없으면 빈 리스트 반환")
    void getRecommendation_returnsEmpty_whenNoClothes() {
        // given
        UUID weatherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Weather weather = mockWeather(
                weatherId,
                20.0, 1.0, 50.0,
                PrecipitationType.NONE, SkyStatus.CLEAR
        );

        User user = mock(User.class);

        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByUserId(user)).thenReturn(Optional.empty());

        when(clothesRepository.findAllByOwnerId(userId)).thenReturn(List.of());

        // when
        RecommendationDto dto = recommendationService.getRecommendation(weatherId, userId);

        // then
        assertThat(dto.weatherId()).isEqualTo(weatherId);
        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.clothes()).isEmpty();
    }

    @Test
    @DisplayName("getRecommendation: 메인 착장 불가면(원피스 없고 상/하의 세트 불가) 빈 리스트 반환")
    void getRecommendation_returnsEmpty_whenNoMainOutfitPossible() {
        // given
        UUID weatherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Weather weather = mockWeather(
                weatherId,
                20.0, 1.0, 50.0,
                PrecipitationType.NONE, SkyStatus.CLEAR
        );

        User user = mock(User.class);

        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByUserId(user)).thenReturn(Optional.empty());

        // TOP만 있고 BOTTOM/DRESS 없음 -> 메인 불가
        Clothes topOnly = mockClothes(UUID.randomUUID(), userId, ClothesType.TOP, attrsWithThickness("보통"));

        when(clothesRepository.findAllByOwnerId(userId)).thenReturn(List.of(topOnly));

        // when
        RecommendationDto dto = recommendationService.getRecommendation(weatherId, userId);

        // then
        assertThat(dto.clothes()).isEmpty();
    }

    @Test
    @DisplayName("getRecommendation: 상의+하의가 있으면 추천 결과에 메인 착장(원피스 or 상의+하의)이 반드시 포함된다")
    void getRecommendation_includesMainOutfit_whenTopAndBottomExist() {
        // given
        UUID weatherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Weather weather = mockWeather(
                weatherId,
                22.0, 1.0, 50.0,
                PrecipitationType.NONE, SkyStatus.CLEAR
        );

        User user = mock(User.class);

        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByUserId(user)).thenReturn(Optional.empty());

        Clothes top = mockClothes(UUID.randomUUID(), userId, ClothesType.TOP, attrsWithThicknessAndColor("보통", "검정"));
        Clothes bottom = mockClothes(UUID.randomUUID(), userId, ClothesType.BOTTOM, attrsWithThicknessAndColor("보통", "하양"));
        Clothes shoes = mockClothes(UUID.randomUUID(), userId, ClothesType.SHOES, List.of());

        when(clothesRepository.findAllByOwnerId(userId)).thenReturn(List.of(top, bottom, shoes));

        // when
        RecommendationDto dto = recommendationService.getRecommendation(weatherId, userId);

        // then
        assertThat(dto.clothes()).isNotEmpty();

        Set<String> types = dto.clothes().stream()
                .map(RecommendedClothesDto::type)
                .collect(Collectors.toSet());

        boolean hasDress = types.contains("DRESS");
        boolean hasTopAndBottom = types.contains("TOP") && types.contains("BOTTOM");

        assertThat(hasDress || hasTopAndBottom)
                .as("추천 결과에는 원피스(DRESS) 또는 상의+하의(TOP+BOTTOM)가 반드시 포함되어야 함")
                .isTrue();
    }

    @Test
    @DisplayName("getRecommendation: 체감온도 <= 17이면 아우터 후보가 있을 때 OUTER가 포함된다")
    void getRecommendation_includesOuter_whenCold() {
        // given
        UUID weatherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Weather weather = mockWeather(
                weatherId,
                16.0, 0.0, 50.0,
                PrecipitationType.NONE, SkyStatus.CLEAR
        );

        User user = mock(User.class);

        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByUserId(user)).thenReturn(Optional.empty());

        Clothes top = mockClothes(UUID.randomUUID(), userId, ClothesType.TOP, attrsWithThickness("두꺼움"));
        Clothes bottom = mockClothes(UUID.randomUUID(), userId, ClothesType.BOTTOM, attrsWithThickness("두꺼움"));
        Clothes outer = mockClothes(UUID.randomUUID(), userId, ClothesType.OUTER, attrsWithThickness("두꺼움"));

        when(clothesRepository.findAllByOwnerId(userId)).thenReturn(List.of(top, bottom, outer));

        // when
        RecommendationDto dto = recommendationService.getRecommendation(weatherId, userId);

        // then
        Set<String> types = dto.clothes().stream()
                .map(RecommendedClothesDto::type)
                .collect(Collectors.toSet());

        assertThat(types).contains("OUTER");
    }



    private Weather mockWeather(
            UUID id,
            double tempCurrent,
            double windSpeed,
            double humidity,
            PrecipitationType precipitationType,
            SkyStatus skyStatus
    ) {
        Weather w = mock(Weather.class);
        lenient().when(w.getId()).thenReturn(id);
        lenient().when(w.getTempCurrent()).thenReturn(tempCurrent);
        lenient().when(w.getWindSpeed()).thenReturn(windSpeed);
        lenient().when(w.getHumidity()).thenReturn(humidity);
        lenient().when(w.getPrecipitationType()).thenReturn(precipitationType);
        lenient().when(w.getSkyStatus()).thenReturn(skyStatus);
        return w;
    }

    private Clothes mockClothes(UUID id, UUID ownerId, ClothesType type, List<ClothesAttribute> attrs) {
        Clothes c = mock(Clothes.class);
        lenient().when(c.getId()).thenReturn(id);
        lenient().when(c.getOwnerId()).thenReturn(ownerId);
        lenient().when(c.getName()).thenReturn("N");
        lenient().when(c.getImageUrl()).thenReturn(null);
        lenient().when(c.getType()).thenReturn(type);
        lenient().when(c.getAttributes()).thenReturn(attrs == null ? List.of() : attrs);

        return c;
    }

    private List<ClothesAttribute> attrsWithThickness(String thickness) {
        return List.of(attr("두께감", thickness));
    }

    private List<ClothesAttribute> attrsWithThicknessAndColor(String thickness, String color) {
        return List.of(
                attr("두께감", thickness),
                attr("색상", color)
        );
    }

    private ClothesAttribute attr(String defName, String value) {
        ClothesAttribute a = mock(ClothesAttribute.class);

        ClothesAttributeDef def = mock(ClothesAttributeDef.class);
        lenient().when(def.getId()).thenReturn(UUID.randomUUID());
        lenient().when(def.getName()).thenReturn(defName);
        lenient().when(def.getValues()).thenReturn(List.of());
        lenient().when(a.getDefinition()).thenReturn(def);
        lenient().when(a.getValue()).thenReturn(value);
        return a;
    }
}
