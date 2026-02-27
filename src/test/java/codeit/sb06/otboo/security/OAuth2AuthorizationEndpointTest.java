package codeit.sb06.otboo.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = OAuth2AuthorizationEndpointTest.TestApp.class,
    properties = {
        "spring.security.oauth2.client.registration.kakao.client-id=test-client",
        "spring.security.oauth2.client.registration.kakao.client-secret=test-secret",
        "spring.security.oauth2.client.registration.kakao.redirect-uri=http://localhost:8080/login/oauth2/code/kakao"
    }
)
@AutoConfigureMockMvc
class OAuth2AuthorizationEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        BatchAutoConfiguration.class
    })
    static class TestApp {
        @Bean
        DummyController dummyController() {
            return new DummyController();
        }
    }

    @org.springframework.web.bind.annotation.RestController
    static class DummyController {
        @org.springframework.web.bind.annotation.GetMapping("/__dummy")
        String dummy() {
            return "ok";
        }
    }

    @Test
    void redirectsToKakaoAuthorizationEndpoint() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/kakao"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", containsString("kauth.kakao.com/oauth/authorize")));
    }
}
