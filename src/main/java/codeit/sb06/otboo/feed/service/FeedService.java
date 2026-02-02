package codeit.sb06.otboo.feed.service;

import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.exception.feed.FeedException;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.service.WeatherService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedService {

  private final UserRepository userRepository;
  private final WeatherService weatherService;
//  private final WeatherRepository weatherRepository;
//  private final ClothesRepository clothesRepository;
  private final FeedRepository feedRepository;

  public FeedService(
      UserRepository userRepository,
    WeatherService weatherService,
//      ClothesRepository clothesRepository,
      FeedRepository feedRepository
  ) {
    this.userRepository = userRepository;
    this.weatherService = weatherService;
//    this.clothesRepository = clothesRepository;
    this.feedRepository = feedRepository;
  }

  @Transactional
  public FeedDto create(FeedCreateRequest request) {
    UUID userId = request.authorId();
    User author = userRepository.findById(userId)
        .orElseThrow(() -> FeedException.userNotFound(userId));

    Weather weather = weatherRepository.findById(request.weatherId())
        .orElseThrow(() -> FeedException.weatherNotFound(request.weatherId()));

    // 중복 제거 + 조회
    List<UUID> uniqueClothesIds = request.clothesIds().stream().distinct().toList();
    List<Clothes> clothes = clothesRepository.findAllById(uniqueClothesIds);

    if (clothes.size() != uniqueClothesIds.size()) {
      throw FeedException.clothesNotFound(uniqueClothesIds);
    }

    Feed feed = Feed.create(author, weather, clothes, request.content());
    Feed saved = feedRepository.save(feed);
    return FeedDto.from(saved);
  }
}
