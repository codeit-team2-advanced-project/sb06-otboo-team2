package codeit.sb06.otboo.user.dto.response;

import codeit.sb06.otboo.user.dto.KakaoAccount;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record KakaoUserResponse(
    Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount,
    Map<String, Object> properties
) {

}
