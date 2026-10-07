package imagelab;

import imagelab.model.GrayImage;
import imagelab.processing.AdaptiveContrast;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa o contraste adaptativo e a validação dos seus parâmetros.
 */
class AdaptiveContrastTest {

    @Test
    void deveManterRegiaoConstante() {
        GrayImage image = new GrayImage(new int[][]{
                {100, 100, 100},
                {100, 100, 100},
                {100, 100, 100}
        });

        GrayImage result = AdaptiveContrast.apply(image, 2, 3);

        // Em uma região sem variação, o resultado deve permanecer igual.
        assertEquals(100, result.get(1, 1));
    }

    @Test
    void deveExecutarContrasteAdaptativo() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 100, 10},
                {10, 10, 10}
        });

        GrayImage result = AdaptiveContrast.apply(image, 1, 3);

        // Verifica o resultado produzido pela fórmula adaptativa atual.
        assertEquals(23, result.get(1, 1));
    }

    @Test
    void deveAumentarOContrasteComC() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 10, 10},
                {10, 100, 10},
                {10, 10, 10}
        });

        GrayImage resultC1 = AdaptiveContrast.apply(image, 1, 3);
        GrayImage resultC2 = AdaptiveContrast.apply(image, 2, 3);

        // Aumentar c deve aumentar a resposta do pixel que se
        // encontra acima da média local.
        int valorC1 = resultC1.get(1, 1);
        int valorC2 = resultC2.get(1, 1);

        assertEquals(23, valorC1);
        assertEquals(26, valorC2);
    }

    @Test
    void deveAceitarVizinhançaUm() {
        GrayImage image = new GrayImage(new int[][]{
                {50}
        });

        GrayImage result = AdaptiveContrast.apply(image, 2, 1);

        assertEquals(50, result.get(0, 0));
    }

    @Test
    void deveLimitarResultadoAoIntervaloValido() {
        GrayImage image = new GrayImage(new int[][]{
                {0, 0, 0},
                {0, 255, 0},
                {0, 0, 0}
        });

        GrayImage result = AdaptiveContrast.apply(image, 100, 3);

        int valor = result.get(1, 1);

        // Uma imagem de 8 bits nunca pode sair de [0,255].
        assertEquals(255, valor);
    }

    @Test
    void deveRejeitarCInvalido() {
        GrayImage image = new GrayImage(new int[][]{
                {50}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> AdaptiveContrast.apply(image, 0, 3)
        );
    }

    @Test
    void deveRejeitarVizinhançaPar() {
        GrayImage image = new GrayImage(new int[][]{
                {50}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> AdaptiveContrast.apply(image, 1, 2)
        );
    }
}