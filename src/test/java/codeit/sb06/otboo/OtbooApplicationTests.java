package codeit.sb06.otboo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("context-load")
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "context-load")
class OtbooApplicationTests {

    @Test
    void contextLoads() {
    }

}
