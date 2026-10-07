package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.util.PixelUtils;

/**
 * Implementa transformações pontuais de intensidade.
 *
 * Cada pixel da imagem de saída é calculado a partir
 * principalmente do valor do mesmo pixel na imagem de entrada.
 *
 * Inclui negativo, limiarização, transformação gama,
 * transformação logarítmica e expansão de contraste.
 */
public final class IntensityTransformations {

    private IntensityTransformations() {
    }

    /**
     * Inverte a intensidade dos pixels.
     *
     * A transformação é:
     * s = 255 - r
     */
    public static GrayImage negative(GrayImage image) {
        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                result.set(
                        x,
                        y,
                        255 - image.get(x, y)
                );
            }
        }

        return result;
    }

    /**
     * Realiza uma transformação por limiar.
     *
     * Pixels maiores ou iguais ao limiar recebem 255;
     * os demais recebem 0.
     */
    public static GrayImage threshold(
            GrayImage image,
            int threshold
    ) {
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException(
                    "O limiar deve estar entre 0 e 255."
            );
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                result.set(
                        x,
                        y,
                        image.get(x, y) >= threshold ? 255 : 0
                );
            }
        }

        return result;
    }

    /**
     * Aplica a transformação gama:
     *
     * s = c * r^gamma
     *
     * Os valores são normalizados para o intervalo [0, 1]
     * durante o cálculo e depois retornam para [0, 255].
     */
    public static GrayImage gamma(
            GrayImage image,
            double gamma,
            double c
    ) {
        if (gamma <= 0) {
            throw new IllegalArgumentException(
                    "Gamma deve ser maior que zero."
            );
        }

        if (c <= 0) {
            throw new IllegalArgumentException(
                    "A constante c do gamma deve ser maior que zero."
            );
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double r = image.get(x, y) / 255.0;
                double value = c * Math.pow(r, gamma) * 255.0;

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
     * Aplica a transformação logarítmica:
     *
     * s = c * log(1 + r)
     */
    public static GrayImage logarithm(
            GrayImage image,
            double c
    ) {
        if (c <= 0) {
            throw new IllegalArgumentException(
                    "c deve ser maior que zero."
            );
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double value =
                        c * Math.log1p(image.get(x, y));

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
     * Realiza expansão de contraste por transformação linear
     * definida pelos pontos (r1,s1) e (r2,s2).
     */
    public static GrayImage contrastStretch(
            GrayImage image,
            int r1,
            int s1,
            int r2,
            int s2
    ) {
        if (r1 < 0 || r1 > 254 || r2 < 1 || r2 > 255
                || s1 < 0 || s1 > 255 || s2 < 0 || s2 > 255) {
            throw new IllegalArgumentException(
                    "r1, s1, r2 e s2 devem estar entre 0 e 255; r1 deve ser menor que r2."
            );
        }

        if (r1 >= r2) {
            throw new IllegalArgumentException(
                    "É necessário r1 < r2."
            );
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int r = image.get(x, y);
                double s;

                if (r <= r1) {
                    s = r1 == 0
                            ? s1
                            : (double) s1 * r / r1;
                } else if (r <= r2) {
                    s = s1
                            + (double) (s2 - s1)
                            * (r - r1)
                            / (r2 - r1);
                } else {
                    s = s2
                            + (double) (255 - s2)
                            * (r - r2)
                            / (255 - r2);
                }

                result.set(
                        x,
                        y,
                        PixelUtils.clamp(s)
                );
            }
        }

        return result;
    }
}