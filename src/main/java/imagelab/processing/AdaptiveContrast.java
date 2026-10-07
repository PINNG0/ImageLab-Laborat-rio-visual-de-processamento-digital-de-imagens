package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.util.BorderHandler;
import imagelab.util.PixelUtils;

/**
 * Implementa controle de contraste adaptativo por vizinhança.
 * Para cada pixel são calculados a média e o desvio padrão locais.
 */
public final class AdaptiveContrast {

    private AdaptiveContrast() {
    }

    /**
     * Aplica a normalização local:
     * s = média + c * (r - média) / desvio_padrão.
     * Regiões sem variação são preservadas.
     */
    public static GrayImage apply(GrayImage image, double c, int size) {
        if (image == null) {
            throw new IllegalArgumentException("A imagem não pode ser nula.");
        }
        if (c <= 0) {
            throw new IllegalArgumentException("c deve ser positivo.");
        }
        if (size < 1 || size % 2 == 0) {
            throw new IllegalArgumentException("A vizinhança deve ser ímpar e positiva.");
        }

        GrayImage result = new GrayImage(image.getWidth(), image.getHeight());
        int[][] pixels = image.copyPixels();
        int radius = size / 2;
        int n = size * size;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double sum = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        sum += BorderHandler.getReplicated(pixels, x + dx, y + dy);
                    }
                }

                double mean = sum / n;
                double variance = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        double value = BorderHandler.getReplicated(pixels, x + dx, y + dy);
                        double difference = value - mean;
                        variance += difference * difference;
                    }
                }

                double standardDeviation = Math.sqrt(variance / n);
                double value;
                if (standardDeviation < 1e-9) {
                    value = image.get(x, y);
                } else {
                    value = mean + c * (image.get(x, y) - mean) / standardDeviation;
                }

                result.set(x, y, PixelUtils.clamp(value));
            }
        }

        return result;
    }
}
