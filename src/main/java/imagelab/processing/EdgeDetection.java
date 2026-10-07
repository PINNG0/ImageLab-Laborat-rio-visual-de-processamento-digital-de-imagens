package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.model.Kernel;
import imagelab.model.ProcessingResult;
import imagelab.util.BorderHandler;
import imagelab.util.PixelUtils;

/**
 * Implementa operadores de detecção de bordas.
 *
 * Inclui Roberts, Sobel, Kirsch e Laplaciano.
 */
public final class EdgeDetection {

    private EdgeDetection() {
    }

    /**
     * Aplica o operador de Roberts.
     */
    public static ProcessingResult roberts(GrayImage image) {
        Kernel gx = new Kernel(new double[][]{
                {1, 0},
                {0, -1}
        });

        Kernel gy = new Kernel(new double[][]{
                {0, 1},
                {-1, 0}
        });

        double[][] signedX = signedConvolution(image, gx);
        double[][] signedY = signedConvolution(image, gy);

        GrayImage x = toClampedImage(signedX);
        GrayImage y = toClampedImage(signedY);
        GrayImage magnitude = magnitude(signedX, signedY);

        ProcessingResult result = new ProcessingResult(magnitude);
        result.addIntermediate("Gx", x);
        result.addIntermediate("Gy", y);
        result.addIntermediate("Magnitude", magnitude);

        return result;
    }

    /**
     * Aplica o operador de Sobel.
     *
     * Mantém Gx e Gy antes do cálculo da magnitude.
     */
    public static ProcessingResult sobel(GrayImage image) {
        Kernel gx = new Kernel(new double[][]{
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        });

        Kernel gy = new Kernel(new double[][]{
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        });

        double[][] signedX = signedConvolution(image, gx);
        double[][] signedY = signedConvolution(image, gy);

        GrayImage x = toClampedImage(signedX);
        GrayImage y = toClampedImage(signedY);
        GrayImage magnitude = magnitude(signedX, signedY);

        ProcessingResult result = new ProcessingResult(magnitude);
        result.addIntermediate("Gx", x);
        result.addIntermediate("Gy", y);
        result.addIntermediate("Magnitude", magnitude);

        return result;
    }

    /**
     * Aplica as oito máscaras direcionais de Kirsch.
     *
     * A resposta final é a maior resposta entre as direções.
     */
    public static ProcessingResult kirsch(GrayImage image) {
        double[][][] masks = {
                {
                        {5, 5, 5},
                        {-3, 0, -3},
                        {-3, -3, -3}
                },
                {
                        {5, 5, -3},
                        {5, 0, -3},
                        {-3, -3, -3}
                },
                {
                        {5, -3, -3},
                        {5, 0, -3},
                        {5, -3, -3}
                },
                {
                        {-3, -3, -3},
                        {5, 0, -3},
                        {5, 5, -3}
                },
                {
                        {-3, -3, -3},
                        {-3, 0, -3},
                        {5, 5, 5}
                },
                {
                        {-3, -3, -3},
                        {-3, 0, 5},
                        {-3, 5, 5}
                },
                {
                        {-3, -3, 5},
                        {-3, 0, 5},
                        {-3, -3, 5}
                },
                {
                        {-3, 5, 5},
                        {-3, 0, 5},
                        {-3, -3, -3}
                }
        };

        GrayImage finalImage = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        ProcessingResult result =
                new ProcessingResult(finalImage);

        double[][][] responses =
                new double[masks.length][][];

        String[] directions = {"N", "NE", "E", "SE", "S", "SO", "O", "NO"};

        for (int i = 0; i < masks.length; i++) {
            responses[i] = signedConvolution(
                    image,
                    new Kernel(masks[i])
            );

            result.addIntermediate(
                    "Kirsch " + directions[i],
                    toClampedImage(responses[i])
            );
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                double max = Double.NEGATIVE_INFINITY;

                for (double[][] response : responses) {
                    max = Math.max(max, response[y][x]);
                }

                finalImage.set(
                        x,
                        y,
                        PixelUtils.clamp(max)
                );
            }
        }

        return result;
    }

    /**
     * Aplica o operador Laplaciano.
     */
    public static ProcessingResult laplacian(GrayImage image) {
        Kernel kernel = new Kernel(new double[][]{
                {0, 1, 0},
                {1, -4, 1},
                {0, 1, 0}
        });

        double[][] response =
                signedConvolution(image, kernel);

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                result.set(
                        x,
                        y,
                        PixelUtils.clamp(Math.abs(response[y][x]))
                );
            }
        }

        ProcessingResult output =
                new ProcessingResult(result);

        output.addIntermediate(
                "Laplaciano",
                result
        );

        return output;
    }

    /**
     * Executa a convolução sem limitar o resultado intermediário.
     *
     * Isso é importante para que respostas negativas não sejam
     * perdidas antes do cálculo da magnitude.
     */
    private static double[][] signedConvolution(
            GrayImage image,
            Kernel kernel
    ) {
        double[][] result = new double[
                image.getHeight()
        ][
                image.getWidth()
        ];

        int centerX = kernel.getWidth() / 2;
        int centerY = kernel.getHeight() / 2;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                double sum = 0;

                for (int ky = 0; ky < kernel.getHeight(); ky++) {
                    for (int kx = 0; kx < kernel.getWidth(); kx++) {

                        int imageX =
                                x + kx - centerX;

                        int imageY =
                                y + ky - centerY;

                        int pixel =
                                BorderHandler.getReplicated(
                                        image,
                                        imageX,
                                        imageY
                                );

                        sum += pixel
                                * kernel.getValue(kx, ky);
                    }
                }

                result[y][x] = sum;
            }
        }

        return result;
    }

    /**
     * Converte uma resposta assinada para uma imagem visível.
     */
    private static GrayImage toClampedImage(
            double[][] values
    ) {
        GrayImage result = new GrayImage(
                values[0].length,
                values.length
        );

        for (int y = 0; y < values.length; y++) {
            for (int x = 0; x < values[y].length; x++) {
                result.set(
                        x,
                        y,
                        PixelUtils.clamp(
                                Math.abs(values[y][x])
                        )
                );
            }
        }

        return result;
    }

    /**
     * Calcula a magnitude das respostas Gx e Gy.
     */
    private static GrayImage magnitude(
            double[][] gx,
            double[][] gy
    ) {
        GrayImage result = new GrayImage(
                gx[0].length,
                gx.length
        );

        for (int y = 0; y < gx.length; y++) {
            for (int x = 0; x < gx[y].length; x++) {

                double value = Math.sqrt(
                        gx[y][x] * gx[y][x]
                                + gy[y][x] * gy[y][x]
                );

                result.set(
                        x,
                        y,
                        PixelUtils.clamp(value)
                );
            }
        }

        return result;
    }
}