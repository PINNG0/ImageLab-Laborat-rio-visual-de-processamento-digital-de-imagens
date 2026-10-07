package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.model.Kernel;
import imagelab.util.BorderHandler;
import imagelab.util.PixelUtils;

/**
 * Implementa a operação de convolução espacial.
 *
 * A operação segue a ideia:
 *
 * g(x,y) = f(x,y) * h(x,y) + offset
 *
 * Cada pixel da imagem é calculado utilizando os pixels
 * da sua vizinhança e os coeficientes da máscara.
 *
 * As posições fora da imagem utilizam replicação das bordas.
 * O resultado final é limitado ao intervalo [0,255].
 */
public final class Convolution {

    private Convolution() {
    }

    /**
     * Aplica a convolução utilizando o offset armazenado no Kernel.
     */
    public static GrayImage apply(
            GrayImage image,
            Kernel kernel
    ) {
        return apply(
                image,
                kernel,
                kernel.getOffset()
        );
    }

    /**
     * Aplica a convolução utilizando um offset informado.
     *
     * Fórmula:
     *
     * g = f * h + offset
     */
    public static GrayImage apply(
            GrayImage image,
            Kernel kernel,
            double offset
    ) {
        if (image == null) {
            throw new IllegalArgumentException(
                    "A imagem não pode ser nula."
            );
        }

        if (kernel == null) {
            throw new IllegalArgumentException(
                    "O kernel não pode ser nulo."
            );
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int[][] input =
                image.getPixels();

        int[][] output =
                new int[height][width];

        int kernelWidth =
                kernel.getWidth();

        int kernelHeight =
                kernel.getHeight();

        int centerX =
                kernelWidth / 2;

        int centerY =
                kernelHeight / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double sum = 0.0;

                for (int ky = 0;
                     ky < kernelHeight;
                     ky++) {

                    for (int kx = 0;
                         kx < kernelWidth;
                         kx++) {

                        int imageX =
                                x + kx - centerX;

                        int imageY =
                                y + ky - centerY;

                        int pixel =
                                BorderHandler.getReplicated(
                                        input,
                                        imageX,
                                        imageY
                                );

                        sum +=
                                pixel
                                        * kernel.getValue(
                                                kx,
                                                ky
                                        );
                    }
                }

                sum += offset;

                output[y][x] =
                        PixelUtils.clamp(sum);
            }
        }

        return new GrayImage(output);
    }

    /**
     * Aplica uma convolução utilizando uma matriz de coeficientes.
     */
    public static GrayImage apply(
            GrayImage image,
            double[][] kernel
    ) {
        return apply(
                image,
                new Kernel(kernel),
                0.0
        );
    }

    /**
     * Aplica uma convolução utilizando uma matriz de coeficientes
     * e um offset.
     */
    public static GrayImage apply(
            GrayImage image,
            double[][] kernel,
            double offset
    ) {
        return apply(
                image,
                new Kernel(kernel),
                offset
        );
    }
}