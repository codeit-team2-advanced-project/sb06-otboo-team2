# sb06-otboo-team2 
- [GitHub Issue](https://github.com/codeit-team2-advanced-project/sb06-otboo-team2/issues)
- [Github Project](https://github.com/orgs/codeit-team2-advanced-project/projects/2/views/3)

## 팀원 구성
- 이호건 ([Github 링크](https://github.com/HOGUN00))
- 김태헌
- 서경원 ([Github 링크](https://github.com/SeoGyeongWon))
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
### 1. 실시간 DM 및 알림 시스템 구축
* **통신 목적에 따른 프로토콜 이원화**: 양방향성이 중요한 DM에는 WebSocket을, 단방향성 알림에는 가벼운 SSE를 채택하여 서버 리소스 및 통신 효율 최적화
* **분산 환경 세션 불일치 해결**: 로드밸런싱 환경에서 발생하는 인메모리 세션(SseEmitter, WebSocketSession) 공유 문제를 메시지 브로커로 해결
* **연결 안정성 보장**: 주기적인 Ping 전송으로 연결 끊김을 방지하고, `lastEventId`를 활용해 재연결 시 미수신 메시지 보정

### 2. Redis Stream 기반 메시지 브로커 설계
* **Redis Stream 채택**: 인메모리 기반의 빠른 응답 속도와 Consumer Group, ACK, PEL(Pending Entries List) 기능을 활용해 메시지 유실 없는 아키텍처 설계
* **장애 전파 차단 (Resilience)**: `Resilience4j` 서킷 브레이커를 도입하여 레디스 장애 시 어플리케이션으로의 장애 전파 차단
* **메시지 복구 및 직렬화 최적화**: 미처리 메시지(PEL) 재전송 스케줄러를 구현하고, `ObjectMapper` 설정을 통해 JavaTime 직렬화 및 클래스 타입 매핑 오류 해결
* **메모리 파편화 관리**: 키별 메시지 개수 제한(Maxlen)과 `activedefrag` 설정을 통해 인메모리 데이터베이스 효율성 향상

### 3. Spring Batch 대용량 통계 및 스케줄링 최적화
* **카테시안 곱(Cartesian Product) 해결**: 1:N 연관관계 조인 시 발생하는 데이터 폭발 문제를 스칼라 서브쿼리와 DTO 프로젝션으로 해결하여 I/O 성능 개선
* **배치 안정성 확보**: `LazyInitializationException` 및 N+1 문제를 차단하고, `ShedLock`을 도입하여 다중 서버 환경에서 배치 중복 실행 방지

### 4. 코드 품질 관리
* **SonarQube 정적 분석**
   - 코드 스멜 및 보안 취약점을 자동 검증하여 기술적 부채 관리
   - GitHub Actions를 연동하여 PR 단위로 **테스트 커버리지 80%** 및 빌드 통과를 강제하여 코드 품질 상향 평준화
* **상호 보완적 코드 리뷰**: 기계적 검증은 SonarQube에 맡기고, 팀원 리뷰 시에는 도메인 로직의 정합성과 더 나은 설계 제안에 집중

---

### 김태헌
- 

---

### 서경원

### 1. 댓글 생성 및 조회

### 2. 팔로우 생성, 취소 및 팔로워 팔로잉 조회
  
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
