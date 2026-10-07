package imagelab;

import imagelab.model.GrayImage;
import imagelab.model.ProcessingResult;
import imagelab.processing.Sharpening;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa aguçamento por Laplaciano e high boost.
 */
class SharpeningTest {

    @Test
    void deveExecutarAguçamentoLaplaciano() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 100, 10},
                {10, 10, 10}
        });

        ProcessingResult result =
                Sharpening.laplacian(image, true);

        assertNotNull(result.getImage());
        assertNotNull(
                result.getIntermediates().get("Laplaciano")
        );
    }

    @Test
    void deveExecutarHighBoost() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 100, 10},
                {10, 10, 10}
        });

        GrayImage result =
                Sharpening.highBoost(image, 1, 1);

        assertNotNull(result);
        // High boost: original + máscara de detalhes.
        // No centro, 100 + (100 - 20) = 180.
        assertEquals(180, result.get(1, 1));    }

    @Test
    void deveRejeitarCInvalido() {
        GrayImage image = new GrayImage(new int[][]{
                {10}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> Sharpening.highBoost(image, 0, 1)
        );
    }
}