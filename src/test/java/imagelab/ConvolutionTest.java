package imagelab;

import imagelab.model.GrayImage;
import imagelab.model.Kernel;
import imagelab.processing.Convolution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa a operação de convolução espacial.
 *
 * Inclui uma máscara identidade, os testes obrigatórios
 * de aguçamento, relevo e detecção de bordas, além do
 * teste do offset da convolução.
 */
class ConvolutionTest {

    @Test
    void deveAplicarMascaraIdentidade() {
        GrayImage image = new GrayImage(new int[][]{
                {10, 20, 30},
                {40, 50, 60},
                {70, 80, 90}
        });

        Kernel identity =
                new Kernel(new double[][]{
                        {0, 0, 0},
                        {0, 1, 0},
                        {0, 0, 0}
                }, 0);

        GrayImage result =
                Convolution.apply(
                        image,
                        identity
                );

        assertEquals(
                50,
                result.get(1, 1)
        );
    }

    @Test
    void deveTestarAguçamentoComCDIguaisAUm() {
        int c = 1;
        int d = 1;

        Kernel sharpening =
                new Kernel(new double[][]{
                        {-c, -c, -c},
                        {-c, 8 * c + d, -c},
                        {-c, -c, -c}
                });

        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 10, 10},
                        {10, 20, 10},
                        {10, 10, 10}
                });

        GrayImage result =
                Convolution.apply(
                        image,
                        sharpening
                );

        // A soma dos coeficientes é d = 1.
        assertEquals(
                1.0,
                sharpening.getSum()
        );

        // 20 * 9 - 8 * 10 = 100.
        assertEquals(
                100,
                result.get(1, 1)
        );
    }

    @Test
void deveTestarAguçamentoComOutrosValoresDeC() {
    int c = 2;
    int d = 1;

    Kernel sharpening =
            new Kernel(new double[][]{
                    {-c, -c, -c},
                    {-c, 8 * c + d, -c},
                    {-c, -c, -c}
            });

    GrayImage image =
            new GrayImage(new int[][]{
                    {10, 10, 10},
                    {10, 20, 10},
                    {10, 10, 10}
            });

    GrayImage result =
            Convolution.apply(
                    image,
                    sharpening
            );

    // A soma dos coeficientes continua sendo d = 1.
    assertEquals(
            1.0,
            sharpening.getSum()
    );

    // O teste verifica a resposta produzida pela máscara
    // com c = 2 para o pixel central.
    assertEquals(
            180,
            result.get(1, 1)
    );
}

    @Test
    void deveAplicarMascaraDeRelevoH1() {
        Kernel h1 =
                new Kernel(new double[][]{
                        {0, 0, 0},
                        {0, 1, 0},
                        {0, 0, -1}
                });

        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 20, 30},
                        {40, 100, 60},
                        {70, 80, 20}
                });

        GrayImage result =
                Convolution.apply(
                        image,
                        h1
                );

        // 100 - 20 = 80.
        assertEquals(
                80,
                result.get(1, 1)
        );
    }


    @Test
    void deveAplicarMascaraDeRelevoH2() {
        Kernel h2 =
                new Kernel(new double[][]{
                        {0, 0, 2},
                        {0, -1, 0},
                        {-1, 0, 0}
                });

        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 20, 100},
                        {40, 30, 60},
                        {20, 80, 90}
                });

        GrayImage result =
                Convolution.apply(
                        image,
                        h2
                );

        // 2 * 100 - 30 - 20 = 150.
        assertEquals(
                150,
                result.get(1, 1)
        );
    }

    @Test
    void deveAplicarMascaraDeBordasH3() {
        Kernel h3 =
                new Kernel(new double[][]{
                        {-1, -1, 0},
                        {1, 0, 1},
                        {0, 1, 1}
                });

        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 20, 30},
                        {40, 50, 60},
                        {70, 80, 90}
                });

        GrayImage result =
                Convolution.apply(
                        image,
                        h3
                );

        // -10 - 20 + 40 + 60 + 80 + 90 = 240.
        assertEquals(
                240,
                result.get(1, 1)
        );
    }

    @Test
    void deveAplicarMascaraDeBordasH4() {
        Kernel h4 =
                new Kernel(new double[][]{
                        {0, -1, 0},
                        {-1, -4, -1},
                        {0, -1, 0}
                });

        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 20, 30},
                        {40, 50, 60},
                        {70, 80, 90}
                });

        GrayImage result =
                Convolution.apply(
                        image,
                        h4
                );

        // O resultado matemático é -400.
        // A imagem final deve permanecer no intervalo [0,255].
        assertEquals(
                0,
                result.get(1, 1)
        );
    }

    @Test
    void deveAplicarOffsetDoKernel() {
        GrayImage image =
                new GrayImage(new int[][]{
                        {10, 20, 30},
                        {40, 50, 60},
                        {70, 80, 90}
                });

        Kernel identityWithOffset =
                new Kernel(
                        new double[][]{
                                {0, 0, 0},
                                {0, 1, 0},
                                {0, 0, 0}
                        },
                        25
                );

        GrayImage result =
                Convolution.apply(
                        image,
                        identityWithOffset
                );

        // A identidade mantém 50 e o offset adiciona 25.
        assertEquals(
                75,
                result.get(1, 1)
        );
    }
}