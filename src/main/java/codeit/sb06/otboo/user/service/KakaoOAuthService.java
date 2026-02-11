package codeit.sb06.otboo.user.service;

import codeit.sb06.otboo.user.dto.response.KakaoTokenResponse;
import codeit.sb06.otboo.user.dto.response.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public KakaoTokenResponse exchangeCodeForToken(String code) {
        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=authorization_code" +
                        "&client_id=" + kakaoClientId +
                        "&client_secret=" + kakaoClientSecret +
                        "&redirect_uri=" + kakaoRedirectUri +
                        "&code=" + code)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    public KakaoUserResponse fetchUserInfo(String accessToken){
        return webClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(KakaoUserResponse.class)
            .block();
    }

}
