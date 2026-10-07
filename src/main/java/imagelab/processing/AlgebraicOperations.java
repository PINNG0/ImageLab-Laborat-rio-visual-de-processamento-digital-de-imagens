package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.util.PixelUtils;

/**
 * Implementa operações algébricas entre imagens em escala de cinza.
 *
 * Inclui dissolve cruzado uniforme e não uniforme.
 */
public final class AlgebraicOperations {

    private AlgebraicOperations() {
    }

    /**
     * Realiza o dissolve cruzado uniforme entre duas imagens.
     *
     * Fórmula:
     *
     * g = (1 - alpha) * f1 + alpha * f2
     *
     * alpha = 0 mantém a primeira imagem.
     * alpha = 1 produz a segunda imagem.
     */
    public static GrayImage dissolveUniform(
            GrayImage image1,
            GrayImage image2,
            double alpha
    ) {
        validateImages(image1, image2);

        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException(
                    "alpha deve estar entre 0 e 1."
            );
        }

        GrayImage result =
                new GrayImage(
                        image1.getWidth(),
                        image1.getHeight()
                );

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {

                double value =
                        (1 - alpha) * image1.get(x, y)
                                + alpha * image2.get(x, y);

                result.set(
                        x,
                        y,
                        PixelUtils.clamp(value)
                );
            }
        }

        return result;
    }

    /**
     * Realiza dissolve cruzado não uniforme.
     *
     * Cada pixel possui um peso diferente, obtido da imagem
     * de pesos e normalizado para o intervalo [0,1].
     *
     * Fórmula:
     *
     * g = (1 - w) * f1 + w * f2
     */
    public static GrayImage dissolveNonUniform(
            GrayImage image1,
            GrayImage image2,
            GrayImage weight
    ) {
        validateImages(image1, image2);

        if (weight == null) {
            throw new IllegalArgumentException(
                    "A imagem de pesos não pode ser nula."
            );
        }

        if (weight.getWidth() != image1.getWidth()
                || weight.getHeight() != image1.getHeight()) {

            throw new IllegalArgumentException(
                    "As três imagens devem possuir as mesmas dimensões."
            );
        }

        GrayImage result =
                new GrayImage(
                        image1.getWidth(),
                        image1.getHeight()
                );

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {

                double weightValue =
                        weight.get(x, y) / 255.0;

                double value =
                        (1 - weightValue) * image1.get(x, y)
                                + weightValue * image2.get(x, y);

                result.set(
                        x,
                        y,
                        PixelUtils.clamp(value)
                );
            }
        }

        return result;
    }

    /**
     * Verifica se as duas imagens existem e possuem
     * as mesmas dimensões.
     */
    private static void validateImages(
            GrayImage image1,
            GrayImage image2
    ) {
        if (image1 == null || image2 == null) {
            throw new IllegalArgumentException(
                    "As imagens não podem ser nulas."
            );
        }

        if (image1.getWidth() != image2.getWidth()
                || image1.getHeight() != image2.getHeight()) {

            throw new IllegalArgumentException(
                    "As imagens devem possuir as mesmas dimensões."
            );
        }
    }
}