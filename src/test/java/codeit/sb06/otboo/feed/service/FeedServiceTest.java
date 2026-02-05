package codeit.sb06.otboo.feed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.entity.ClothesType;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.exception.clothes.ClothesNotFoundException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.exception.weather.WeatherNotFoundException;
import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.entity.FeedClothes;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.dto.weather.PrecipitationType;
import codeit.sb06.otboo.weather.dto.weather.SkyStatus;
import codeit.sb06.otboo.weather.dto.weather.WindStrength;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private ClothesRepository clothesRepository;

    @Mock
    private FeedRepository feedRepository;

    private UUID authorId;
    private UUID weatherId;
    private User author;
    private Weather weather;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        weatherId = UUID.randomUUID();

        author = new User(
            authorId,
            "user@example.com",
            "user",
            Role.USER,
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            "password",
            null,
            null
        );

        weather = Weather.builder()
            .id(weatherId)
            .skyStatus(SkyStatus.CLEAR)
            .precipitationType(PrecipitationType.NONE)
            .precipitationAmount(0.0)
            .precipitationProbability(0.0)
            .tempCurrent(15.0)
            .tempMin(10.0)
            .tempMax(20.0)
            .humidity(30.0)
            .windSpeed(2.0)
            .windStrength(WindStrength.WEAK)
            .build();
    }

    @Test
    void createFeed_success() {
        UUID clothesId1 = UUID.randomUUID();
        UUID clothesId2 = UUID.randomUUID();
        List<UUID> clothesIds = List.of(clothesId1, clothesId2);

        Clothes clothes1 = new Clothes(authorId, "shirt", "img1", ClothesType.TOP);
        Clothes clothes2 = new Clothes(authorId, "pants", "img2", ClothesType.BOTTOM);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(clothesRepository.findAllById(any())).thenReturn(List.of(clothes1, clothes2));
        when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> invocation.getArgument(0));

    FeedDto result = feedService.create(request);

    verify(feedRepository).save(any(Feed.class));
    assertNotNull(result);
    assertEquals("content", result.content());
    assertEquals(authorId, result.author().userId());
    assertEquals(2, result.ootds().size());
  }

    @Test
    void createFeed_mapsFeedClothesToJoinEntities() {
        UUID clothesId1 = UUID.randomUUID();
        UUID clothesId2 = UUID.randomUUID();
        List<UUID> clothesIds = List.of(clothesId1, clothesId2);

        Clothes clothes1 = new Clothes(authorId, "shirt", "img1", ClothesType.TOP);
        Clothes clothes2 = new Clothes(authorId, "pants", "img2", ClothesType.BOTTOM);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(clothesRepository.findAllById(any())).thenReturn(List.of(clothes1, clothes2));
        when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> invocation.getArgument(0));

        feedService.create(request);

        ArgumentCaptor<Feed> captor = ArgumentCaptor.forClass(Feed.class);
        verify(feedRepository).save(captor.capture());
        Feed saved = captor.getValue();

        assertEquals(2, saved.getFeedClothes().size());
        Set<Clothes> mappedClothes = saved.getFeedClothes().stream()
            .map(FeedClothes::getClothes)
            .collect(Collectors.toSet());
        assertEquals(Set.of(clothes1, clothes2), mappedClothes);
    }

    @Test
    void createFeed_allowsDuplicateClothesIds() {
        UUID clothesId1 = UUID.randomUUID();
        List<UUID> clothesIds = List.of(clothesId1, clothesId1);

        Clothes clothes1 = new Clothes(authorId, "shirt", "img1", ClothesType.TOP);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(clothesRepository.findAllById(any())).thenReturn(List.of(clothes1));
        when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> invocation.getArgument(0));

        feedService.create(request);

        ArgumentCaptor<Feed> captor = ArgumentCaptor.forClass(Feed.class);
        verify(feedRepository).save(captor.capture());
        Feed saved = captor.getValue();

        assertEquals(1, saved.getFeedClothes().size());
        assertEquals(clothes1, saved.getFeedClothes().get(0).getClothes());
    }

  @Test
  void createFeed_throwsWhenClothesMissing() {
    UUID clothesId1 = UUID.randomUUID();
    UUID clothesId2 = UUID.randomUUID();
    List<UUID> clothesIds = List.of(clothesId1, clothesId2);

        Clothes clothes1 = new Clothes(authorId, "shirt", "img1", ClothesType.TOP);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(weatherRepository.findById(weatherId)).thenReturn(Optional.of(weather));
        when(clothesRepository.findAllById(any())).thenReturn(List.of(clothes1));

        assertThrows(ClothesNotFoundException.class, () -> feedService.create(request));
    }

    @Test
    void createFeed_throwsWhenUserMissing() {
        UUID clothesId1 = UUID.randomUUID();
        List<UUID> clothesIds = List.of(clothesId1);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> feedService.create(request));
    }

    @Test
    void createFeed_throwsWhenWeatherMissing() {
        UUID clothesId1 = UUID.randomUUID();
        List<UUID> clothesIds = List.of(clothesId1);

        Clothes clothes1 = new Clothes(authorId, "shirt", "img1", ClothesType.TOP);

        FeedCreateRequest request = new FeedCreateRequest(
            authorId,
            weatherId,
            clothesIds,
            "content"
        );

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(weatherRepository.findById(weatherId)).thenReturn(Optional.empty());

        assertThrows(WeatherNotFoundException.class, () -> feedService.create(request));
    }
}
