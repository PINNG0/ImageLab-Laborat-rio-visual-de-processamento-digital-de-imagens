package imagelab;

import imagelab.model.GrayImage;
import imagelab.processing.IntensityTransformations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa as transformações pontuais de intensidade.
 */
class IntensityTransformationsTest {

    @Test
    void deveAplicarNegativo() {
        GrayImage image = new GrayImage(new int[][]{
                {0, 100, 255}
        });

        GrayImage result = IntensityTransformations.negative(image);

        assertEquals(255, result.get(0, 0));
        assertEquals(155, result.get(1, 0));
        assertEquals(0, result.get(2, 0));
    }

    @Test
    void deveAplicarThreshold() {
        GrayImage image = new GrayImage(new int[][]{
                {49, 50, 51}
        });

        GrayImage result =
                IntensityTransformations.threshold(image, 50);

        assertEquals(0, result.get(0, 0));
        assertEquals(255, result.get(1, 0));
        assertEquals(255, result.get(2, 0));
    }

    @Test
    void deveAplicarGammaUmSemAlterarAImagem() {
        GrayImage image = new GrayImage(new int[][]{
                {0, 64, 128, 255}
        });

        GrayImage result =
                IntensityTransformations.gamma(image, 1, 1);

        assertEquals(0, result.get(0, 0));
        assertEquals(64, result.get(1, 0));
        assertEquals(128, result.get(2, 0));
        assertEquals(255, result.get(3, 0));
    }

    @Test
    void deveAplicarGammaMenorQueUm() {
        GrayImage image = new GrayImage(new int[][]{
                {64}
        });

        GrayImage result =
                IntensityTransformations.gamma(image, 0.5, 1);

        // Gamma < 1 aumenta a intensidade de um valor intermediário.
        assertEquals(128, result.get(0, 0));
    }

    @Test
    void deveRejeitarGammaInvalido() {
        GrayImage image = new GrayImage(new int[][]{{100}});

        assertThrows(
                IllegalArgumentException.class,
                () -> IntensityTransformations.gamma(image, 0, 1)
        );
    }

    @Test
    void deveRejeitarConstanteGammaInvalida() {
        GrayImage image = new GrayImage(new int[][]{{100}});

        assertThrows(
                IllegalArgumentException.class,
                () -> IntensityTransformations.gamma(image, 1, 0)
        );
    }

    @Test
    void deveAplicarLogaritmo() {
        GrayImage image = new GrayImage(new int[][]{
                {0, 1, 10}
        });

        GrayImage result =
                IntensityTransformations.logarithm(image, 1);

        assertEquals(0, result.get(0, 0));
        assertEquals(1, result.get(1, 0));
        assertEquals(2, result.get(2, 0));
    }

    @Test
    void deveRejeitarConstanteLogaritmicaInvalida() {
        GrayImage image = new GrayImage(new int[][]{{100}});

        assertThrows(
                IllegalArgumentException.class,
                () -> IntensityTransformations.logarithm(image, 0)
        );
    }

    @Test
    void deveAplicarExpansaoDeContraste() {
        GrayImage image = new GrayImage(new int[][]{
                {50, 125, 200, 255}
        });

        GrayImage result =
                IntensityTransformations.contrastStretch(
                        image,
                        50, 0,
                        200, 255
                );

        assertEquals(0, result.get(0, 0));
        assertEquals(128, result.get(1, 0));
        assertEquals(255, result.get(2, 0));
        assertEquals(255, result.get(3, 0));
    }

    @Test
    void deveTratarR1IgualAZero() {
        GrayImage image = new GrayImage(new int[][]{{0, 100, 255}});

        GrayImage result =
                IntensityTransformations.contrastStretch(
                        image,
                        0, 25,
                        255, 230
                );

        assertEquals(25, result.get(0, 0));
        assertEquals(105, result.get(1, 0));
        assertEquals(230, result.get(2, 0));
    }

    @Test
    void deveRejeitarPontosDeContrasteInvalidos() {
        GrayImage image = new GrayImage(new int[][]{{100}});

        assertThrows(
                IllegalArgumentException.class,
                () -> IntensityTransformations.contrastStretch(
                        image,
                        200, 0,
                        100, 255
                )
        );
    }
}