package codeit.sb06.otboo.feed.service;

import codeit.sb06.otboo.exception.auth.ForbiddenException;
import codeit.sb06.otboo.exception.feed.FeedNotFoundException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.exception.weather.WeatherNotFoundException;
import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.exception.clothes.ClothesNotFoundException;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

  private final UserRepository userRepository;
  private final WeatherRepository weatherRepository;
  private final ClothesRepository clothesRepository;
  private final FeedRepository feedRepository;

  @Transactional
  public FeedDto create(FeedCreateRequest request) {
    UUID userId = request.authorId();
    User author = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());

    Weather weather = weatherRepository.findById(request.weatherId())
        .orElseThrow(() -> new WeatherNotFoundException(request.weatherId()));

    List<Clothes> clothes = loadClothesOrThrow(request.clothesIds());

    Feed feed = Feed.create(author, weather, clothes, request.content());
    Feed saved = feedRepository.save(feed);
    return FeedDto.from(saved);
  }

  @Transactional
  public void delete(UUID feedId) {
    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(() -> new FeedNotFoundException(feedId));
    feedRepository.delete(feed);
  }

  @Transactional
  public FeedDto update(UUID feedId, UUID currentUserId, String content) {
    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(() -> new FeedNotFoundException(feedId));

    if (!feed.getUser().getId().equals(currentUserId)) {
      User user = userRepository.findById(currentUserId)
          .orElseThrow(UserNotFoundException::new);
      if (user.getRole() != Role.ADMIN) {
        throw new ForbiddenException();
      }
    }

    feed.updateContent(content);
    return FeedDto.from(feed);
  }

  private List<Clothes> loadClothesOrThrow(List<UUID> ids) {
    List<UUID> uniqueIds = ids.stream().distinct().toList();
    List<Clothes> clothes = clothesRepository.findAllById(uniqueIds);

    if (clothes.size() != uniqueIds.size()) {
      Set<UUID> found = clothes.stream()
          .map(Clothes::getId)
          .collect(Collectors.toSet());
      List<UUID> missing = uniqueIds.stream()
          .filter(id -> !found.contains(id))
          .toList();
      throw new ClothesNotFoundException(missing);
    }

    return clothes;
  }
}
