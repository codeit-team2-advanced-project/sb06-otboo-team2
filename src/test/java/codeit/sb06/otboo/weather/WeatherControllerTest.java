package codeit.sb06.otboo.weather;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import codeit.sb06.otboo.security.jwt.JwtAuthenticationFilter;
import codeit.sb06.otboo.weather.controller.WeatherController;
import codeit.sb06.otboo.weather.service.WeatherService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@Disabled("모든 항목")
@WebMvcTest(WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
class WeatherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private WeatherService weatherService;

  @MockBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void 현재_날씨_요청_파라미터_검증_실패() throws Exception {
    mockMvc.perform(get("/api/weathers")
            .param("longitude", "127.0")
            .param("latitude", "120.0"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 위치_요청_파라미터_검증_실패() throws Exception {
    mockMvc.perform(get("/api/weathers/location")
            .param("longitude", "200.0")
            .param("latitude", "37.5"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 현재_날씨_요청_성공시_서비스가_호출된다() throws Exception {
    mockMvc.perform(get("/api/weathers")
            .param("longitude", "126.9780")
            .param("latitude", "37.5665"))
        .andExpect(status().isOk());

    then(weatherService).should().getCurrentWeather(126.9780, 37.5665);
  }
}
