package imagelab;

import imagelab.model.GrayImage;
import imagelab.processing.AlgebraicOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa as operações algébricas entre imagens.
 *
 * Os testes verificam os casos extremos e intermediários
 * dos dissolves uniforme e não uniforme.
 */
class AlgebraicOperationsTest {

    @Test
    void deveManterPrimeiraImagemComAlphaZero() {
        GrayImage image1 = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {100, 120},
                {140, 160}
        });

        GrayImage result =
                AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        0.0
                );

        // Com alpha = 0, somente a primeira imagem participa.
        assertEquals(10, result.get(0, 0));
        assertEquals(20, result.get(1, 0));
        assertEquals(30, result.get(0, 1));
        assertEquals(40, result.get(1, 1));
    }

    @Test
    void deveManterSegundaImagemComAlphaUm() {
        GrayImage image1 = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {100, 120},
                {140, 160}
        });

        GrayImage result =
                AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        1.0
                );

        // Com alpha = 1, somente a segunda imagem participa.
        assertEquals(100, result.get(0, 0));
        assertEquals(120, result.get(1, 0));
        assertEquals(140, result.get(0, 1));
        assertEquals(160, result.get(1, 1));
    }

    @Test
    void deveCalcularDissolveUniforme() {
        GrayImage image1 = new GrayImage(new int[][]{
                {0, 0},
                {0, 0}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {200, 200},
                {200, 200}
        });

        GrayImage result =
                AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        0.5
                );

        // Com alpha = 0,5, o resultado é a média das imagens.
        assertEquals(100, result.get(0, 0));
        assertEquals(100, result.get(1, 0));
        assertEquals(100, result.get(0, 1));
        assertEquals(100, result.get(1, 1));
    }

    @Test
    void deveCalcularDissolveNaoUniforme() {
        GrayImage image1 = new GrayImage(new int[][]{
                {0, 0},
                {0, 0}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {200, 200},
                {200, 200}
        });

        GrayImage weight = new GrayImage(new int[][]{
                {0, 128},
                {255, 64}
        });

        GrayImage result =
                AlgebraicOperations.dissolveNonUniform(
                        image1,
                        image2,
                        weight
                );

        // Peso 0 corresponde a 0% da segunda imagem.
        assertEquals(0, result.get(0, 0));

        // Peso 128/255 corresponde aproximadamente a 50%.
        assertEquals(100, result.get(1, 0));

        // Peso 255 corresponde a 100% da segunda imagem.
        assertEquals(200, result.get(0, 1));

        // Peso 64/255 produz aproximadamente 25% da segunda imagem.
        assertEquals(50, result.get(1, 1));
    }

    @Test
    void deveRejeitarAlphaForaDoIntervalo() {
        GrayImage image1 = new GrayImage(new int[][]{
                {10}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {20}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        -0.1
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        1.1
                )
        );
    }

    @Test
    void deveRejeitarImagensComDimensoesDiferentes() {
        GrayImage image1 = new GrayImage(new int[][]{
                {10, 20}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {30},
                {40}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> AlgebraicOperations.dissolveUniform(
                        image1,
                        image2,
                        0.5
                )
        );
    }

    @Test
    void deveRejeitarPesoComDimensaoDiferente() {
        GrayImage image1 = new GrayImage(new int[][]{
                {10, 20},
                {30, 40}
        });

        GrayImage image2 = new GrayImage(new int[][]{
                {100, 120},
                {140, 160}
        });

        GrayImage weight = new GrayImage(new int[][]{
                {255}
        });

        assertThrows(
                IllegalArgumentException.class,
                () -> AlgebraicOperations.dissolveNonUniform(
                        image1,
                        image2,
                        weight
                )
        );
    }
}