package codeit.sb06.otboo.feed.service;

import codeit.sb06.otboo.exception.auth.ForbiddenException;
import codeit.sb06.otboo.exception.feed.FeedNotFoundException;
import codeit.sb06.otboo.exception.user.UserNotFoundException;
import codeit.sb06.otboo.exception.weather.WeatherNotFoundException;
import codeit.sb06.otboo.feed.dto.FeedCreateRequest;
import codeit.sb06.otboo.feed.dto.FeedDto;
import codeit.sb06.otboo.feed.dto.FeedDtoCursorRequest;
import codeit.sb06.otboo.feed.dto.FeedDtoCursorResponse;
import codeit.sb06.otboo.feed.dto.FeedSortBy;
import codeit.sb06.otboo.feed.dto.FeedSortDirection;
import codeit.sb06.otboo.exception.clothes.ClothesNotFoundException;
import codeit.sb06.otboo.feed.entity.Feed;
import codeit.sb06.otboo.feed.repository.FeedRepository;
import codeit.sb06.otboo.clothes.entity.Clothes;
import codeit.sb06.otboo.clothes.repository.ClothesRepository;
import codeit.sb06.otboo.follow.entity.Follow;
import codeit.sb06.otboo.follow.repository.FollowRepository;
import codeit.sb06.otboo.notification.publisher.NotificationEventPublisher;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.entity.User;
import codeit.sb06.otboo.user.repository.UserRepository;
import codeit.sb06.otboo.weather.entity.Weather;
import codeit.sb06.otboo.weather.repository.WeatherRepository;
import codeit.sb06.otboo.feed.entity.FeedLike;
import codeit.sb06.otboo.feed.repository.FeedLikeRepository;
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
  private final FeedLikeRepository feedLikeRepository;
  private final FollowRepository followRepository;
  private final NotificationEventPublisher notificationEventPublisher;

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

    followRepository.findByFolloweeId(author.getId())
            .stream()
            .map(Follow::getFollower)
            .forEach(follower -> notificationEventPublisher.publishFolloweeFeedPostedEvent(
                    follower.getId(),
                    author.getName(),
                    feed.getContent().substring(0, Math.min(10, feed.getContent().length()))
            ));

    return FeedDto.from(saved);
  }

  @Transactional(readOnly = true)
  public FeedDtoCursorResponse getFeeds(UUID currentUserId, FeedDtoCursorRequest request) {
    int limit = request.limit();
    FeedSortBy sortBy = request.resolveSortBy();
    FeedSortDirection sortDirection = request.sortDirection();

    List<Feed> feeds = feedRepository.findFeedListByCursor(request, limit + 1);
    boolean hasNext = feeds.size() > limit;
    if (hasNext) {
      feeds = feeds.subList(0, limit);
    }

    List<UUID> feedIds = feeds.stream().map(Feed::getId).toList();
    Set<UUID> likedFeedIds = feedIds.isEmpty()
        ? Set.of()
        : Set.copyOf(feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(currentUserId, feedIds));

    String nextCursor = null;
    UUID nextIdAfter = null;
    if (hasNext && !feeds.isEmpty()) {
      Feed last = feeds.get(feeds.size() - 1);
      nextCursor = cursorValue(last, sortBy);
      nextIdAfter = last.getId();
    }

    long totalCount = feedRepository.countFeedList(request);
    return FeedDtoCursorResponse.of(
        feeds,
        likedFeedIds,
        nextCursor,
        nextIdAfter,
        hasNext,
        totalCount,
        sortBy,
        sortDirection
    );
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

  @Transactional
  public void like(UUID feedId, UUID currentUserId) {
    Feed feed = getFeedOrThrow(feedId);
    User user = getUserOrThrow(currentUserId);

    if (feedLikeRepository.existsByFeedIdAndUserId(feedId, currentUserId)) {
      return;
    }

    feedLikeRepository.save(FeedLike.create(user, feed));
    feed.incrementLikeCount();

    notificationEventPublisher.publishFeedLikedEvent(
            feed.getUser().getId(),
            feed.getContent().substring(0, Math.min(10, feed.getContent().length())),
            user.getName()
    );
  }

  @Transactional
  public void unlike(UUID feedId, UUID currentUserId) {
    Feed feed = getFeedOrThrow(feedId);
    getUserOrThrow(currentUserId);

    feedLikeRepository.findByFeedIdAndUserId(feedId, currentUserId)
        .ifPresent(feedLike -> {
          feedLikeRepository.delete(feedLike);
          feed.decrementLikeCount();
        });
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

  private Feed getFeedOrThrow(UUID feedId) {
    return feedRepository.findById(feedId)
        .orElseThrow(() -> new FeedNotFoundException(feedId));
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
  }

  private String cursorValue(Feed feed, FeedSortBy sortBy) {
    if (sortBy == FeedSortBy.LIKECOUNT) {
      return String.valueOf(feed.getLikeCount());
    }
    return feed.getCreatedAt().toString();
  }
}
