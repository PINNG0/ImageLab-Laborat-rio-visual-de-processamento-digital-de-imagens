package imagelab;

import imagelab.model.GrayImage;
import imagelab.model.ProcessingResult;
import imagelab.processing.EdgeDetection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa os operadores de detecção de bordas.
 */
class EdgeDetectionTest {

    private GrayImage image() {
        return new GrayImage(new int[][]{
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {255, 255, 255, 255, 255},
                {255, 255, 255, 255, 255},
                {255, 255, 255, 255, 255}
        });
    }

    @Test
    void deveExecutarRoberts() {
        ProcessingResult result =
                EdgeDetection.roberts(image());

        assertNotNull(result.getImage());
        assertEquals(5, result.getImage().getWidth());
        assertEquals(5, result.getImage().getHeight());
    }

    @Test
    void deveExecutarSobelComGxGyEMagnitude() {
        ProcessingResult result =
                EdgeDetection.sobel(image());

        assertNotNull(result.getImage());
        assertNotNull(result.getIntermediates().get("Gx"));
        assertNotNull(result.getIntermediates().get("Gy"));
        assertNotNull(result.getIntermediates().get("Magnitude"));
    }

    @Test
    void deveExecutarKirsch() {
        ProcessingResult result =
                EdgeDetection.kirsch(image());

        assertNotNull(result.getImage());
    }

    @Test
    void deveExecutarLaplaciano() {
        ProcessingResult result =
                EdgeDetection.laplacian(image());

        assertNotNull(result.getImage());
        assertEquals(5, result.getImage().getWidth());
    }
}