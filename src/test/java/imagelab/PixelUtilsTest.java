package imagelab;

import imagelab.util.PixelUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa as funções auxiliares de tratamento de pixels.
 */
class PixelUtilsTest {

    @Test
    void clampDeveManterValoresNoIntervalo() {
        assertEquals(0, PixelUtils.clamp(-50));
        assertEquals(128, PixelUtils.clamp(128));
        assertEquals(255, PixelUtils.clamp(300));
    }
}
