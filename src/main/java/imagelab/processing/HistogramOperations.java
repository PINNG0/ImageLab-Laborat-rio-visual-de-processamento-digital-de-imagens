package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.util.PixelUtils;

/**
 * Implementa operações relacionadas ao histograma de uma imagem.
 *
 * O histograma representa a quantidade de pixels existente
 * em cada nível de intensidade de 0 a 255.
 */
public final class HistogramOperations {

    private HistogramOperations() {
    }

    /**
     * Calcula o histograma da imagem.
     *
     * @return vetor com 256 posições, uma para cada intensidade
     */
    public static int[] histogram(GrayImage image) {
        int[] histogram = new int[256];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                histogram[PixelUtils.clamp(image.get(x, y))]++;
            }
        }

        return histogram;
    }

    /**
     * Realiza expansão linear de contraste.
     *
     * O menor nível presente passa para 0 e o maior
     * nível presente passa para 255.
     */
    public static GrayImage expansion(GrayImage image) {
        int[] histogram = histogram(image);

        int min = 0;
        while (min < 256 && histogram[min] == 0) {
            min++;
        }

        int max = 255;
        while (max >= 0 && histogram[max] == 0) {
            max--;
        }

        if (min >= max) {
            return image.copy();
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int value = (int) Math.round(
                        (image.get(x, y) - min)
                                * 255.0
                                / (max - min)
                );

                result.set(x, y, PixelUtils.clamp(value));
            }
        }

        return result;
    }

    /**
     * Realiza equalização do histograma utilizando a função
     * de distribuição acumulada (CDF).
     */
    public static GrayImage equalization(GrayImage image) {
        int[] histogram = histogram(image);
        int total = image.getWidth() * image.getHeight();

        int[] cdf = new int[256];
        int accumulated = 0;

        for (int i = 0; i < 256; i++) {
            accumulated += histogram[i];
            cdf[i] = accumulated;
        }

        int firstNonZero = 0;

        while (firstNonZero < 256
                && histogram[firstNonZero] == 0) {
            firstNonZero++;
        }

        if (firstNonZero == 256
                || cdf[firstNonZero] == total) {
            return image.copy();
        }

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        int denominator = total - cdf[firstNonZero];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int r = image.get(x, y);

                int value = (int) Math.round(
                        ((cdf[r] - cdf[firstNonZero])
                                / (double) denominator) * 255
                );

                result.set(x, y, PixelUtils.clamp(value));
            }
        }

        return result;
    }
}