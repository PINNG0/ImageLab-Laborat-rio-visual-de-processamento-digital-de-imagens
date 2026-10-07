package imagelab.model;

import java.util.Arrays;

/**
 * Representa uma imagem em escala de cinza de 8 bits.
 *
 * Os pixels são armazenados internamente em uma matriz no formato:
 * pixels[y][x]
 *
 * Cada pixel possui um valor entre 0 e 255.
 * O valor 0 representa preto e o valor 255 representa branco.
 */
public class GrayImage {

    private final int width;
    private final int height;
    private final int[][] pixels;

    /**
     * Cria uma imagem vazia com a largura e altura informadas.
     * Inicialmente todos os pixels possuem valor 0.
     */
    public GrayImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "Largura e altura devem ser maiores que zero."
            );
        }

        this.width = width;
        this.height = height;
        this.pixels = new int[height][width];
    }

    /**
     * Cria uma imagem a partir de uma matriz de pixels.
     *
     * Uma cópia da matriz é criada para evitar que alterações externas
     * modifiquem diretamente os dados internos da imagem.
     */
    public GrayImage(int[][] pixels) {
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            throw new IllegalArgumentException(
                    "A matriz de pixels não pode ser vazia."
            );
        }

        this.height = pixels.length;
        this.width = pixels[0].length;
        this.pixels = new int[height][width];

        for (int y = 0; y < height; y++) {
            if (pixels[y] == null || pixels[y].length != width) {
                throw new IllegalArgumentException(
                        "Todas as linhas devem possuir a mesma largura."
                );
            }

            for (int x = 0; x < width; x++) {
                this.pixels[y][x] = clamp(pixels[y][x]);
            }
        }
    }

    /**
     * Retorna a largura da imagem em pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retorna a altura da imagem em pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retorna o valor do pixel na posição (x, y).
     */
    public int getPixel(int x, int y) {
        checkCoordinates(x, y);
        return pixels[y][x];
    }

    /**
     * Altera o valor do pixel na posição (x, y).
     *
     * O valor é limitado ao intervalo [0, 255].
     */
    public void setPixel(int x, int y, int value) {
        checkCoordinates(x, y);
        pixels[y][x] = clamp(value);
    }

    /**
     * Retorna uma cópia da matriz de pixels.
     *
     * A cópia impede que o código externo altere diretamente
     * a matriz interna da imagem.
     */
    public int[][] getPixels() {
        int[][] copy = new int[height][width];

        for (int y = 0; y < height; y++) {
            copy[y] = Arrays.copyOf(pixels[y], width);
        }

        return copy;
    }

    /**
     * Cria uma nova imagem com os mesmos pixels da imagem atual.
     */
    public GrayImage copy() {
        return new GrayImage(getPixels());
    }

    /**
     * Retorna o pixel usando a forma tradicional get(x, y).
     */
    public int get(int x, int y) {
        return getPixel(x, y);
    }

    /**
     * Altera o pixel usando a forma tradicional set(x, y, valor).
     */
    public void set(int x, int y, int value) {
        setPixel(x, y, value);
    }

    /**
     * Retorna uma cópia da matriz de pixels.
     */
    public int[][] copyPixels() {
        return getPixels();
    }

    /**
     * Verifica se as coordenadas estão dentro dos limites da imagem.
     */
    private void checkCoordinates(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException(
                    "Coordenada fora dos limites: (" + x + ", " + y + ")"
            );
        }
    }

    /**
     * Garante que um valor de pixel permaneça no intervalo de 0 a 255.
     */
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}