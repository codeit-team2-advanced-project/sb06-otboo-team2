# sb06-otboo-team2 
- [GitHub Issue](https://github.com/codeit-team2-advanced-project/sb06-otboo-team2/issues)
- [Github Project](https://github.com/orgs/codeit-team2-advanced-project/projects/2/views/3)

## 팀원 구성
- 이호건 ([Github 링크](https://github.com/HOGUN00))
- 김태헌
- 서경원
- 이현욱
- 조동현

---

## 프로젝트 소개

- 옷장을 부탁해: 날씨, 취향을 고려해 사용자가 보유한 의상 조합을 추천해주고, OOTD 피드, 팔로우 등의 소셜 기능을 갖춘 서비스
- 프로젝트 기간: 2026.01.22 ~ 2026.02.27

---

## 기술 스택

-
-
-

---

## 팀원별 구현 기능 상세

### 이호건
- 

---

### 김태헌
- 

---

### 서경원
- 
  
---

### 이현욱
- 

---
 
### 조동현
- 
---

## 파일 구조

```text
otboo/
├── gradle/
│   ├── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── codeit/
│   │   │       ├── sb06/
│   │   │           ├── otboo/
│   │   │               ├── clothes/
│   │   │               │   ├── controller/
│   │   │               │   │   ├── ClothesAttributeDefController.java
│   │   │               │   │   ├── ClothesController.java
│   │   │               │   │   └── RecommendationController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── ClothesAttributeDefCreateRequest.java
│   │   │               │   │   ├── ClothesAttributeDefDto.java
│   │   │               │   │   ├── ClothesAttributeDefUpdateRequest.java
│   │   │               │   │   ├── ClothesAttributeDto.java
│   │   │               │   │   ├── ClothesAttributeWithDefDto.java
│   │   │               │   │   ├── ClothesCreateRequest.java
│   │   │               │   │   ├── ClothesDto.java
│   │   │               │   │   ├── ClothesDtoCursorResponse.java
│   │   │               │   │   ├── ClothesUpdateRequest.java
│   │   │               │   │   ├── RecommendationDto.java
│   │   │               │   │   └── RecommendedClothesDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Clothes.java
│   │   │               │   │   ├── ClothesAttribute.java
│   │   │               │   │   ├── ClothesAttributeDef.java
│   │   │               │   │   ├── ClothesAttributeDefValue.java
│   │   │               │   │   └── ClothesType.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── ClothesAttributeDefRepository.java
│   │   │               │   │   ├── ClothesAttributeRepository.java
│   │   │               │   │   ├── ClothesQueryRepository.java
│   │   │               │   │   ├── ClothesQueryRepositoryImpl.java
│   │   │               │   │   └── ClothesRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── ClothesAttributeDefService.java
│   │   │               │       ├── ClothesService.java
│   │   │               │       └── RecommendationService.java
│   │   │               ├── comment/
│   │   │               │   ├── controller/
│   │   │               │   │   └── CommentController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── AuthorDto.java
│   │   │               │   │   ├── CommentCreateRequest.java
│   │   │               │   │   ├── CommentDto.java
│   │   │               │   │   └── CommentDtoCursorResponse.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Comment.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── CommentQueryRepository.java
│   │   │               │   │   ├── CommentQueryRepositoryImpl.java
│   │   │               │   │   └── CommentRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── BasicCommentService.java
│   │   │               │       └── CommentService.java
│   │   │               ├── common/
│   │   │               │   ├── scheduler/
│   │   │               │       └── AbstractStreamRecoveryScheduler.java
│   │   │               ├── config/
│   │   │               │   ├── AdminInitializer.java
│   │   │               │   ├── CustomFeignRetryer.java
│   │   │               │   ├── FeignClientConfig.java
│   │   │               │   ├── FeignErrorDecoder.java
│   │   │               │   ├── JpaAuditingConfig.java
│   │   │               │   ├── QueryDslConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   ├── RedisStreamManager.java
│   │   │               │   ├── SchedulingConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── ShedLockConfig.java
│   │   │               │   └── WebMvcConfig.java
│   │   │               ├── exception/
│   │   │               │   ├── auth/
│   │   │               │   │   ├── AuthException.java
│   │   │               │   │   ├── ForbiddenException.java
│   │   │               │   │   ├── InvalidTokenException.java
│   │   │               │   │   └── InvalidUserDetailException.java
│   │   │               │   ├── clothes/
│   │   │               │   │   ├── ClothesAlreadyExistsException.java
│   │   │               │   │   ├── ClothesAttributeDefNotFoundException.java
│   │   │               │   │   ├── ClothesBadRequestException.java
│   │   │               │   │   ├── ClothesException.java
│   │   │               │   │   ├── ClothesForbiddenException.java
│   │   │               │   │   ├── ClothesImageUploadFailedException.java
│   │   │               │   │   ├── ClothesNotFoundException.java
│   │   │               │   │   ├── InvalidClothesAttributeValueException.java
│   │   │               │   │   └── InvalidClothesTypeException.java
│   │   │               │   ├── comment/
│   │   │               │   │   ├── CommentCreateFailException.java
│   │   │               │   │   ├── CommentException.java
│   │   │               │   │   └── CommentListReadFailException.java
│   │   │               │   ├── feed/
│   │   │               │   │   ├── FeedException.java
│   │   │               │   │   └── FeedNotFoundException.java
│   │   │               │   ├── follow/
│   │   │               │   │   ├── FollowCancelFailException.java
│   │   │               │   │   ├── FollowException.java
│   │   │               │   │   ├── FollowNotFoundException.java
│   │   │               │   │   └── SelfFollowDeniedException.java
│   │   │               │   ├── message/
│   │   │               │   │   ├── ChatRoomNotFoundException.java
│   │   │               │   │   ├── DirectMessageMappingException.java
│   │   │               │   │   └── MessageException.java
│   │   │               │   ├── notification/
│   │   │               │   │   ├── NotificationBatchException.java
│   │   │               │   │   ├── NotificationException.java
│   │   │               │   │   └── NotificationMappingException.java
│   │   │               │   ├── profile/
│   │   │               │   │   ├── ProfileException.java
│   │   │               │   │   ├── ProfileNotFoundException.java
│   │   │               │   │   ├── ProfileS3NotFound.java
│   │   │               │   │   └── S3UploadFailedException.java
│   │   │               │   ├── storage/
│   │   │               │   │   ├── StorageDeleteFailedException.java
│   │   │               │   │   ├── StorageException.java
│   │   │               │   │   └── StorageUploadFailedException.java
│   │   │               │   ├── user/
│   │   │               │   │   ├── LockedUserException.java
│   │   │               │   │   ├── MailSendException.java
│   │   │               │   │   ├── UserAlreadyExistException.java
│   │   │               │   │   ├── UserException.java
│   │   │               │   │   └── UserNotFoundException.java
│   │   │               │   ├── weather/
│   │   │               │   │   ├── WeatherException.java
│   │   │               │   │   └── WeatherNotFoundException.java
│   │   │               │   ├── ErrorResponse.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── RootException.java
│   │   │               ├── feed/
│   │   │               │   ├── controller/
│   │   │               │   │   └── FeedController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── FeedCreateRequest.java
│   │   │               │   │   ├── FeedDto.java
│   │   │               │   │   ├── FeedDtoCursorRequest.java
│   │   │               │   │   ├── FeedDtoCursorResponse.java
│   │   │               │   │   ├── FeedSortBy.java
│   │   │               │   │   ├── FeedSortDirection.java
│   │   │               │   │   ├── FeedUpdateRequest.java
│   │   │               │   │   └── OotdDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Feed.java
│   │   │               │   │   ├── FeedClothes.java
│   │   │               │   │   └── FeedLike.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── FeedLikeRepository.java
│   │   │               │   │   ├── FeedRepository.java
│   │   │               │   │   ├── FeedRepositoryCustom.java
│   │   │               │   │   └── FeedRepositoryImpl.java
│   │   │               │   ├── service/
│   │   │               │       └── FeedService.java
│   │   │               ├── follow/
│   │   │               │   ├── controller/
│   │   │               │   │   └── FollowController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── FollowCreateRequest.java
│   │   │               │   │   ├── FollowDto.java
│   │   │               │   │   ├── FollowListResponse.java
│   │   │               │   │   ├── FollowSummaryDto.java
│   │   │               │   │   ├── FolloweeDto.java
│   │   │               │   │   └── FollowerDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Follow.java
│   │   │               │   │   └── FollowDirection.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── FollowQueryRepository.java
│   │   │               │   │   ├── FollowQueryRepositoryImpl.java
│   │   │               │   │   └── FollowRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── BasicFollowService.java
│   │   │               │       └── FollowService.java
│   │   │               ├── message/
│   │   │               │   ├── config/
│   │   │               │   │   ├── WebSocketConfig.java
│   │   │               │   │   └── WebSocketPoolProperties.java
│   │   │               │   ├── controller/
│   │   │               │   │   ├── DirectMessageController.java
│   │   │               │   │   └── DirectMessageWebSocketController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   └── DirectMessageCreateRequest.java
│   │   │               │   │   ├── response/
│   │   │               │   │   │   └── DirectMessageDtoCursorResponse.java
│   │   │               │   │   ├── DirectMessageCreatedRedisEvent.java
│   │   │               │   │   ├── DirectMessageDto.java
│   │   │               │   │   └── DirectMessageRedisDto.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── ChatMember.java
│   │   │               │   │   ├── ChatRoom.java
│   │   │               │   │   └── DirectMessage.java
│   │   │               │   ├── enums/
│   │   │               │   │   └── SortDirection.java
│   │   │               │   ├── interceptor/
│   │   │               │   │   └── WebSocketChannelInterceptor.java
│   │   │               │   ├── listener/
│   │   │               │   │   ├── DirectMessageEventListener.java
│   │   │               │   │   └── DirectMessageStreamListener.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── DirectMessageMapper.java
│   │   │               │   ├── publisher/
│   │   │               │   │   ├── DirectMessageEventPublisher.java
│   │   │               │   │   └── DirectMessageRedisPublisher.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── ChatMemberRepository.java
│   │   │               │   │   ├── ChatRoomRepository.java
│   │   │               │   │   └── DirectMessageRepository.java
│   │   │               │   ├── scheduler/
│   │   │               │   │   └── DmStreamRecoveryScheduler.java
│   │   │               │   ├── service/
│   │   │               │       ├── impl/
│   │   │               │       │   ├── ChatMemberServiceImpl.java
│   │   │               │       │   ├── ChatRoomServiceImpl.java
│   │   │               │       │   └── DirectMessageServiceImpl.java
│   │   │               │       ├── ChatMemberService.java
│   │   │               │       ├── ChatRoomService.java
│   │   │               │       └── DirectMessageService.java
│   │   │               ├── notification/
│   │   │               │   ├── batch/
│   │   │               │   │   ├── NotificationCleanupBatchConfig.java
│   │   │               │   │   └── UserActivityNotificationBatchConfig.java
│   │   │               │   ├── controller/
│   │   │               │   │   ├── NotificationController.java
│   │   │               │   │   └── SseController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── response/
│   │   │               │   │   │   └── NotificationDtoCursorResponse.java
│   │   │               │   │   ├── NotificationDto.java
│   │   │               │   │   ├── SseEvent.java
│   │   │               │   │   └── StatNotificationDTO.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Notification.java
│   │   │               │   ├── enums/
│   │   │               │   │   ├── NotificationLevel.java
│   │   │               │   │   └── SortDirection.java
│   │   │               │   ├── event/
│   │   │               │   │   ├── ClothesAttributeAddedEvent.java
│   │   │               │   │   ├── DirectMessageCreatedEvent.java
│   │   │               │   │   ├── FeedCommentedEvent.java
│   │   │               │   │   ├── FeedLikedEvent.java
│   │   │               │   │   ├── FollowedEvent.java
│   │   │               │   │   ├── FolloweeFeedPostedEvent.java
│   │   │               │   │   └── RoleUpdatedEvent.java
│   │   │               │   ├── listener/
│   │   │               │   │   ├── NotificationEventListener.java
│   │   │               │   │   └── NotificationStreamListener.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── NotificationMapper.java
│   │   │               │   ├── publisher/
│   │   │               │   │   ├── impl/
│   │   │               │   │   │   ├── NotificationEventPublisherImpl.java
│   │   │               │   │   │   └── RedisNotificationPublisherImpl.java
│   │   │               │   │   ├── NotificationEventPublisher.java
│   │   │               │   │   └── RedisNotificationPublisher.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── NotificationRepository.java
│   │   │               │   │   ├── SseEmitterRepository.java
│   │   │               │   │   └── SseEventCacheRepository.java
│   │   │               │   ├── scheduler/
│   │   │               │   │   ├── NotificationCleanUpBatchScheduler.java
│   │   │               │   │   ├── NotificationStreamRecoveryScheduler.java
│   │   │               │   │   ├── SseHeartbeatScheduler.java
│   │   │               │   │   └── UserActivityNotificationBatchScheduler.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── impl/
│   │   │               │   │   │   ├── NotificationCacheServiceImpl.java
│   │   │               │   │   │   ├── NotificationServiceImpl.java
│   │   │               │   │   │   └── SseServiceImpl.java
│   │   │               │   │   ├── NotificationCacheService.java
│   │   │               │   │   ├── NotificationService.java
│   │   │               │   │   └── SseService.java
│   │   │               │   ├── util/
│   │   │               │       └── SseEventIdGenerator.java
│   │   │               ├── profile/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── LocationDto.java
│   │   │               │   │   ├── ProfileDto.java
│   │   │               │   │   └── ProfileUpdateRequest.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Gender.java
│   │   │               │   │   ├── Location.java
│   │   │               │   │   └── Profile.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── LocationRepository.java
│   │   │               │   │   └── ProfileRepository.java
│   │   │               │   ├── service/
│   │   │               │       ├── ProfileServiceImpl.java
│   │   │               │       └── S3StorageService.java
│   │   │               ├── security/
│   │   │               │   ├── dto/
│   │   │               │   │   ├── JwtDto.java
│   │   │               │   │   └── JwtInformation.java
│   │   │               │   ├── handler/
│   │   │               │   │   ├── Http403ForbiddenAccessDeniedHandler.java
│   │   │               │   │   ├── LoginFailureHandler.java
│   │   │               │   │   ├── OAuth2FailureHandler.java
│   │   │               │   │   ├── OAuth2SuccessHandler.java
│   │   │               │   │   └── SpaCsrfTokenRequestHandler.java
│   │   │               │   ├── jwt/
│   │   │               │   │   ├── AbstractJwtSuccessHandler.java
│   │   │               │   │   ├── InMemoryJwtRegistry.java
│   │   │               │   │   ├── JwtAuthenticationFilter.java
│   │   │               │   │   ├── JwtLoginSuccessHandler.java
│   │   │               │   │   ├── JwtLogoutHandler.java
│   │   │               │   │   ├── JwtRegistry.java
│   │   │               │   │   ├── JwtTokenProvider.java
│   │   │               │   │   └── RedisJwtRegistry.java
│   │   │               │   ├── resolver/
│   │   │               │   │   ├── CurrentUserId.java
│   │   │               │   │   ├── CurrentUserIdArgumentResolver.java
│   │   │               │   │   ├── RequireRole.java
│   │   │               │   │   └── RoleAuthorizationInterceptor.java
│   │   │               │   ├── user/
│   │   │               │       ├── OtbooOidcUserDetails.java
│   │   │               │       ├── OtbooUserDetails.java
│   │   │               │       ├── OtbooUserDetailsService.java
│   │   │               │       └── TemporaryPasswordAuthenticationProvider.java
│   │   │               ├── user/
│   │   │               │   ├── controller/
│   │   │               │   │   ├── AuthController.java
│   │   │               │   │   └── UserController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   ├── ChangePasswordRequest.java
│   │   │               │   │   │   ├── ResetPasswordRequest.java
│   │   │               │   │   │   ├── UserCreateRequest.java
│   │   │               │   │   │   ├── UserLockUpdateRequest.java
│   │   │               │   │   │   ├── UserRoleUpdateRequest.java
│   │   │               │   │   │   └── UserSliceRequest.java
│   │   │               │   │   ├── response/
│   │   │               │   │   │   ├── KakaoTokenResponse.java
│   │   │               │   │   │   ├── KakaoUserResponse.java
│   │   │               │   │   │   └── UserDtoCursorResponse.java
│   │   │               │   │   ├── KakaoAccount.java
│   │   │               │   │   ├── Profile.java
│   │   │               │   │   ├── UserDto.java
│   │   │               │   │   └── UserSummary.java
│   │   │               │   ├── entity/
│   │   │               │   │   ├── Provider.java
│   │   │               │   │   ├── Role.java
│   │   │               │   │   └── User.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── UserRepository.java
│   │   │               │   │   ├── UserRepositoryCustom.java
│   │   │               │   │   └── UserRepositoryImpl.java
│   │   │               │   ├── service/
│   │   │               │       ├── AuthServiceImpl.java
│   │   │               │       ├── CustomOAuth2UserService.java
│   │   │               │       ├── CustomOidcUserService.java
│   │   │               │       ├── KakaoOAuth2UserService.java
│   │   │               │       ├── KakaoOAuthService.java
│   │   │               │       └── UserServiceImpl.java
│   │   │               ├── weather/
│   │   │               │   ├── client/
│   │   │               │   │   ├── KakaoLocationFeignClient.java
│   │   │               │   │   ├── OpenWeatherFeignClient.java
│   │   │               │   │   └── WeatherApiClient.java
│   │   │               │   ├── controller/
│   │   │               │   │   └── WeatherController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── location/
│   │   │               │   │   │   ├── KakaoRegionDocument.java
│   │   │               │   │   │   ├── KakaoRegionResponse.java
│   │   │               │   │   │   └── LocationDto.java
│   │   │               │   │   ├── weather/
│   │   │               │   │       ├── HumidityDto.java
│   │   │               │   │       ├── OpenWeatherForecastApiResponse.java
│   │   │               │   │       ├── PrecipitationDto.java
│   │   │               │   │       ├── PrecipitationType.java
│   │   │               │   │       ├── SkyStatus.java
│   │   │               │   │       ├── TemperatureDto.java
│   │   │               │   │       ├── WeatherDto.java
│   │   │               │   │       ├── WeatherSummaryDto.java
│   │   │               │   │       ├── WindSpeedDto.java
│   │   │               │   │       └── WindStrength.java
│   │   │               │   ├── entity/
│   │   │               │   │   └── Weather.java
│   │   │               │   ├── mapper/
│   │   │               │   │   └── WeatherMapper.java
│   │   │               │   ├── model/
│   │   │               │   │   └── SnapshotCandidate.java
│   │   │               │   ├── repository/
│   │   │               │   │   └── WeatherRepository.java
│   │   │               │   ├── service/
│   │   │               │       └── WeatherService.java
│   │   │               └── OtbooApplication.java
│   │   ├── resources/
│   │       ├── static/
│   │       │   ├── assets/
│   │       │   │   ├── Login BG-CiDhH4iC.svg
│   │       │   │   ├── Logo-wa1Pp3bf.svg
│   │       │   │   ├── index-B6awMYOr.css
│   │       │   │   ├── index-Nj5ghMes.js
│   │       │   │   └── login upper section-DT7EHkpB.svg
│   │       │   ├── index.html
│   │       │   ├── logo_symbol.svg
│   │       │   └── vite.svg
│   │       ├── application-h2.yaml
│   │       └── application.yaml
│   ├── test/
│       ├── java/
│       │   ├── codeit/
│       │       ├── sb06/
│       │           ├── otboo/
│       │               ├── clothes/
│       │               │   ├── repository/
│       │               │   │   ├── ClothesQueryRepositoryTest.java
│       │               │   │   └── ClothesRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── ClothesAttributeDefServiceTest.java
│       │               │       ├── ClothesServiceTest.java
│       │               │       └── RecommendationServiceTest.java
│       │               ├── comment/
│       │               │   ├── controller/
│       │               │   │   ├── CommentCreateControllerTest.java
│       │               │   │   └── CommentQueryControllerTest.java
│       │               │   ├── repository/
│       │               │   │   └── CommentRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── CommentCreateServiceTest.java
│       │               │       └── CommentQueryServiceTest.java
│       │               ├── config/
│       │               │   └── AdminInitializerTest.java
│       │               ├── exception/
│       │               │   └── GlobalExceptionHandlerTest.java
│       │               ├── feed/
│       │               │   ├── repository/
│       │               │   │   └── FeedRepositoryTest.java
│       │               │   ├── service/
│       │               │       └── FeedServiceTest.java
│       │               ├── follow/
│       │               │   ├── controller/
│       │               │   │   ├── FollowCreateResponseTest.java
│       │               │   │   ├── FollowDeleteResponseTest.java
│       │               │   │   ├── FollowGetListResponseTest.java
│       │               │   │   └── FollowGetSummaryResponseTest.java
│       │               │   ├── entity/
│       │               │   │   └── FollowTest.java
│       │               │   ├── repository/
│       │               │   │   └── FollowRepositoryTest.java
│       │               │   ├── service/
│       │               │       ├── FollowCreateServiceTest.java
│       │               │       ├── FollowDeleteTest.java
│       │               │       ├── FollowGetListTest.java
│       │               │       └── FollowGetSummaryServiceTest.java
│       │               ├── message/
│       │               │   ├── controller/
│       │               │   │   └── DirectMessageWebSocketControllerTest.java
│       │               │   ├── entity/
│       │               │   │   └── ChatRoomTest.java
│       │               │   ├── interceptor/
│       │               │   │   └── WebSocketChannelInterceptorTest.java
│       │               │   ├── listener/
│       │               │   │   └── DirectMessageRedisStreamListenerTest.java
│       │               │   ├── publisher/
│       │               │   │   ├── DirectMessagePublisherTest.java
│       │               │   │   └── DirectMessageRedisPublisherTest.java
│       │               │   ├── repository/
│       │               │   │   └── DirectMessageRepositoryTest.java
│       │               │   ├── scheduler/
│       │               │   │   ├── DmStreamRecoverySchedulerListenerTest.java
│       │               │   │   └── DmStreamRecoverySchedulerTest.java
│       │               │   ├── service/
│       │               │       ├── ChatMemberServiceImplTest.java
│       │               │       ├── ChatRoomServiceImplTest.java
│       │               │       └── DirectMessageServiceImplTest.java
│       │               ├── notification/
│       │               │   ├── batch/
│       │               │   │   └── UserActivityNotificationBatchTest.java
│       │               │   ├── config/
│       │               │   │   └── EmbeddedRedisConfig.java
│       │               │   ├── listener/
│       │               │   │   └── NotificationListenerTest.java
│       │               │   ├── publisher/
│       │               │   │   ├── NotificationPublisherTest.java
│       │               │   │   └── RedisNotificationPublisherTest.java
│       │               │   ├── repository/
│       │               │   │   ├── NotificationRepositoryTest.java
│       │               │   │   ├── SseEmitterRepositoryTest.java
│       │               │   │   └── SseEventCacheRepositoryTest.java
│       │               │   ├── scheduler/
│       │               │   │   ├── NotificationCleanUpBatchSchedulerTest.java
│       │               │   │   ├── NotificationCleanUpJobIntegrationTest.java
│       │               │   │   ├── NotificationStreamRecoverySchedulerListenerTest.java
│       │               │   │   ├── NotificationStreamRecoverySchedulerTest.java
│       │               │   │   ├── SseHeartbeatSchedulerTest.java
│       │               │   │   ├── UserActivityNotificationBatchSchedulerTest.java
│       │               │   │   └── UserActivityNotificationJobIntegrationTest.java
│       │               │   ├── service/
│       │               │       ├── NotificationCacheServiceRedisTest.java
│       │               │       ├── NotificationCacheServiceUnitTest.java
│       │               │       ├── NotificationServiceTest.java
│       │               │       └── SseServiceTest.java
│       │               ├── profile/
│       │               │   ├── dto/
│       │               │   │   └── ProfileDtoTest.java
│       │               │   ├── entity/
│       │               │   │   └── GenderTest.java
│       │               │   ├── service/
│       │               │       ├── ProfileServiceImplTest.java
│       │               │       ├── S3StorageServiceIntegrationTest.java
│       │               │       └── S3StorageServiceTest.java
│       │               ├── security/
│       │               │   ├── jwt/
│       │               │   │   ├── JwtAuthenticationFilterTest.java
│       │               │   │   ├── JwtLoginSuccessHandlerTest.java
│       │               │   │   ├── JwtLogoutHandlerTest.java
│       │               │   │   └── JwtTokenProviderTest.java
│       │               │   ├── CsrfTokenEndpointTest.java
│       │               │   ├── CurrentUserIdArgumentResolverTest.java
│       │               │   ├── CustomOAuth2UserServiceTest.java
│       │               │   ├── Http403ForbiddenAccessDeniedHandlerTest.java
│       │               │   ├── InMemoryJwtRegistryTest.java
│       │               │   ├── LoginFailureHandlerTest.java
│       │               │   ├── OAuth2AuthorizationEndpointTest.java
│       │               │   ├── OAuth2FailureHandlerTest.java
│       │               │   ├── OAuth2SuccessHandlerTest.java
│       │               │   ├── OtbooUserDetailsServiceTest.java
│       │               │   ├── OtbooUserDetailsTest.java
│       │               │   ├── RedisJwtRegistryTest.java
│       │               │   ├── RoleAuthorizationInterceptorTest.java
│       │               │   ├── SpaCsrfTokenRequestHandlerTest.java
│       │               │   └── TemporaryPasswordAuthenticationProviderTest.java
│       │               ├── user/
│       │               │   ├── controller/
│       │               │   │   ├── AuthControllerTest.java
│       │               │   │   └── UserControllerTest.java
│       │               │   ├── entity/
│       │               │   │   └── UserTest.java
│       │               │   ├── repository/
│       │               │   │   └── UserRepositoryImplTest.java
│       │               │   ├── service/
│       │               │       ├── AuthServiceImplTest.java
│       │               │       ├── CustomOidcUserServiceTest.java
│       │               │       ├── KakaoOAuth2UserServiceTest.java
│       │               │       ├── KakaoOAuthServiceTest.java
│       │               │       └── UserServiceImplTest.java
│       │               ├── util/
│       │               │   └── EasyRandomUtil.java
│       │               ├── weather/
│       │               │   ├── WeatherApiClientTest.java
│       │               │   ├── WeatherControllerTest.java
│       │               │   ├── WeatherRepositoryTest.java
│       │               │   └── WeatherServiceTest.java
│       │               └── OtbooApplicationTests.java
│       ├── resources/
│           └── application-test.yaml
├── Dockerfile
├── README.md
├── build.gradle
├── docker-compose.yml
├── gradlew
├── gradlew.bat
└── settings.gradle

```

---

## 구현 홈페이지  
(개발한 홈페이지에 대한 링크 게시)  


---

## 프로젝트 회고록  
(제작한 발표자료 링크 혹은 첨부파일 첨부)
