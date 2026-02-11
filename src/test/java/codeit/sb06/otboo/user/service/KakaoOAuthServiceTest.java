package codeit.sb06.otboo.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codeit.sb06.otboo.user.dto.KakaoAccount;
import codeit.sb06.otboo.user.dto.Profile;
import codeit.sb06.otboo.user.dto.response.KakaoTokenResponse;
import codeit.sb06.otboo.user.dto.response.KakaoUserResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class KakaoOAuthServiceTest {

  @Test
  void exchangeCodeForToken_returnsTokenResponse() {
    KakaoOAuthService service = new KakaoOAuthService();
    WebClient webClient = Mockito.mock(WebClient.class);
    WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
    WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

    ReflectionTestUtils.setField(service, "webClient", webClient);
    ReflectionTestUtils.setField(service, "kakaoClientId", "client-id");
    ReflectionTestUtils.setField(service, "kakaoClientSecret", "client-secret");
    ReflectionTestUtils.setField(service, "kakaoRedirectUri", "http://localhost:8080/login/oauth2/code/kakao");

    KakaoTokenResponse expected = new KakaoTokenResponse(
        "access-token", "bearer", "refresh-token", 3600, 1209600, "account_email profile_image profile_nickname"
    );

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("https://kauth.kakao.com/oauth/token")).thenReturn(requestBodySpec);
    when(requestBodySpec.header("Content-Type", "application/x-www-form-urlencoded")).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(
        "grant_type=authorization_code"
            + "&client_id=client-id"
            + "&client_secret=client-secret"
            + "&redirect_uri=http://localhost:8080/login/oauth2/code/kakao"
            + "&code=auth-code"
    )).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(KakaoTokenResponse.class)).thenReturn(Mono.just(expected));

    KakaoTokenResponse result = service.exchangeCodeForToken("auth-code");

    assertEquals("access-token", result.accessToken());
    assertEquals("refresh-token", result.refreshToken());
    verify(webClient).post();
    verify(requestBodyUriSpec).uri("https://kauth.kakao.com/oauth/token");
  }

  @Test
  void fetchUserInfo_returnsUserResponse() {
    KakaoOAuthService service = new KakaoOAuthService();
    WebClient webClient = Mockito.mock(WebClient.class);
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

    ReflectionTestUtils.setField(service, "webClient", webClient);

    KakaoUserResponse expected = new KakaoUserResponse(
        12345L,
        new KakaoAccount("user@example.com", new Profile("tester", "https://img.example/profile.png")),
        Map.of("nickname", "tester")
    );

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri("https://kapi.kakao.com/v2/user/me")).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header("Authorization", "Bearer access-token")).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(KakaoUserResponse.class)).thenReturn(Mono.just(expected));

    KakaoUserResponse result = service.fetchUserInfo("access-token");

    assertEquals(12345L, result.id());
    assertEquals("user@example.com", result.kakaoAccount().email());
    assertEquals("tester", result.kakaoAccount().profile().nickname());
    verify(webClient).get();
    verify(requestHeadersSpec).header("Authorization", "Bearer access-token");
  }
}
