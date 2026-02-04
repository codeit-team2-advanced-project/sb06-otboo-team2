package codeit.sb06.otboo.security;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CsrfTokenEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void csrfEndpointSetsCsrfCookie() throws Exception {
        mockMvc.perform(get("/api/auth/csrf-token"))
            .andExpect(status().isNoContent())
            .andExpect(cookie().exists("XSRF-TOKEN"))
            .andExpect(cookie().value("XSRF-TOKEN", not(isEmptyOrNullString())));
    }
}
