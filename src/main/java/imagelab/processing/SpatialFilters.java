package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.util.BorderHandler;
import imagelab.util.PixelUtils;

import java.util.Arrays;

/**
 * Implementa filtros espaciais baseados em vizinhança.
 *
 * Contém os filtros da média e da mediana, com tamanho
 * de vizinhança parametrizável e replicação das bordas.
 */
public final class SpatialFilters {

    private SpatialFilters() {
    }

    /**
     * Aplica o filtro da média.
     *
     * Para cada pixel, calcula a média dos valores presentes
     * na vizinhança definida pelo tamanho informado.
     *
     * @param image imagem de entrada
     * @param size tamanho da vizinhança, obrigatoriamente ímpar
     * @return imagem resultante após a suavização
     */
    public static GrayImage mean(GrayImage image, int size) {
        validateSize(size);

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        int[][] pixels = image.copyPixels();
        int radius = size / 2;
        int count = size * size;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int sum = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        sum += BorderHandler.getReplicated(
                                pixels,
                                x + dx,
                                y + dy
                        );
                    }
                }

                result.set(
                        x,
                        y,
                        PixelUtils.clamp((double) sum / count)
                );
            }
        }

        return result;
    }

    /**
     * Aplica o filtro da mediana.
     *
     * Os valores da vizinhança são ordenados e o valor central
     * da sequência é utilizado como resultado do pixel.
     *
     * @param image imagem de entrada
     * @param size tamanho da vizinhança, obrigatoriamente ímpar
     * @return imagem resultante após a filtragem
     */
    public static GrayImage median(GrayImage image, int size) {
        validateSize(size);

        GrayImage result = new GrayImage(
                image.getWidth(),
                image.getHeight()
        );

        int[][] pixels = image.copyPixels();
        int radius = size / 2;
        int[] values = new int[size * size];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int index = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        values[index++] = BorderHandler.getReplicated(
                                pixels,
                                x + dx,
                                y + dy
                        );
                    }
                }

                Arrays.sort(values);

                result.set(
                        x,
                        y,
                        values[values.length / 2]
                );
            }
        }

        return result;
    }

    /**
     * Verifica se o tamanho da vizinhança é válido.
     *
     * A vizinhança precisa possuir dimensão ímpar para que
     * exista um único elemento central.
     */
    private static void validateSize(int size) {
        if (size < 1 || size % 2 == 0) {
            throw new IllegalArgumentException(
                    "A vizinhança deve ser ímpar e positiva."
            );
        }
    }
}