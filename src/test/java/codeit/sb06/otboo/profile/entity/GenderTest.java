package codeit.sb06.otboo.profile.entity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GenderTest {

    @Test
    void exposesExpectedValues() {
        assertArrayEquals(new Gender[]{Gender.ETC, Gender.MALE, Gender.FEMALE}, Gender.values());
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
        assertEquals(Gender.ETC, Gender.valueOf("ETC"));
    }
}
