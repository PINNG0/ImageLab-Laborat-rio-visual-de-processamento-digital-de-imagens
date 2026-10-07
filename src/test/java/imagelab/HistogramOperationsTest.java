package imagelab;

import imagelab.model.GrayImage;
import imagelab.processing.HistogramOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa histograma, expansão e equalização.
 */
class HistogramOperationsTest {

    @Test
    void deveCalcularHistograma() {
        GrayImage image = new GrayImage(new int[][]{
                {0, 10, 10},
                {10, 255, 255}
        });

        int[] histogram =
                HistogramOperations.histogram(image);

        assertEquals(1, histogram[0]);
        assertEquals(3, histogram[10]);
        assertEquals(2, histogram[255]);
    }

    @Test
    void deveExpandirContraste() {
        GrayImage image = new GrayImage(new int[][]{
                {50, 100, 150}
        });

        GrayImage result =
                HistogramOperations.expansion(image);

        assertEquals(0, result.get(0, 0));
        assertEquals(128, result.get(1, 0));
        assertEquals(255, result.get(2, 0));
    }

    @Test
    void deveManterImagemComUmaUnicaIntensidade() {
        GrayImage image = new GrayImage(new int[][]{
                {100, 100},
                {100, 100}
        });

        GrayImage result =
                HistogramOperations.expansion(image);

        assertEquals(100, result.get(0, 0));
        assertEquals(100, result.get(1, 1));
    }

    @Test
    void deveEqualizarImagemComDuasIntensidades() {
        GrayImage image = new GrayImage(new int[][]{
                {50, 50},
                {200, 200}
        });

        GrayImage result =
                HistogramOperations.equalization(image);

        assertEquals(0, result.get(0, 0));
        assertEquals(255, result.get(1, 1));
    }
}