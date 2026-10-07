package imagelab;

import imagelab.model.GrayImage;
import imagelab.processing.SpatialFilters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa os filtros espaciais da média e da mediana.
 */
class SpatialFiltersTest {

    @Test
    void deveCalcularFiltroDaMedia() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 20, 10},
                {10, 10, 10}
        });

        GrayImage result = SpatialFilters.mean(image, 3);

        // (8 pixels com 10 + 1 pixel com 20) / 9 = 11,11...
        assertEquals(11, result.get(1, 1));
    }

    @Test
    void deveCalcularFiltroDaMediana() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 200, 10},
                {10, 10, 10}
        });

        GrayImage result = SpatialFilters.median(image, 3);

        // A mediana elimina a influência do valor isolado 200.
        assertEquals(10, result.get(1, 1));
    }

    @Test
    void deveAceitarVizinhançaDeTamanhoUm() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        GrayImage mean = SpatialFilters.mean(image, 1);
        GrayImage median = SpatialFilters.median(image, 1);

        assertEquals(10, mean.get(0, 0));
        assertEquals(40, median.get(1, 1));
    }

    @Test
    void deveRejeitarTamanhoPar() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> SpatialFilters.mean(image, 2)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> SpatialFilters.median(image, 2)
        );
    }

    @Test
    void deveRejeitarTamanhoNaoPositivo() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> SpatialFilters.mean(image, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> SpatialFilters.median(image, -1)
        );
    }
}