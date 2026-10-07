package imagelab.model;

/**
 * Representa uma máscara/kernel utilizada em operações de vizinhança,
 * especialmente convoluções.
 *
 * Os coeficientes são armazenados como double para permitir máscaras
 * com valores inteiros ou fracionários.
 */
public class Kernel {

    private final int width;
    private final int height;
    private final double[][] values;
    private final int offset;

    /**
     * Cria um kernel sem offset.
     */
    public Kernel(double[][] values) {
        this(values, 0);
    }

    /**
     * Cria um kernel com uma matriz de coeficientes e um offset.
     *
     * O offset é mantido junto ao kernel para que as classes que
     * utilizam essa forma de construção possam acessá-lo.
     */
    public Kernel(double[][] values, int offset) {
        if (values == null || values.length == 0 || values[0].length == 0) {
            throw new IllegalArgumentException(
                    "A matriz do kernel não pode ser vazia."
            );
        }

        this.height = values.length;
        this.width = values[0].length;
        this.offset = offset;
        this.values = new double[height][width];

        for (int y = 0; y < height; y++) {
            if (values[y] == null || values[y].length != width) {
                throw new IllegalArgumentException(
                        "Todas as linhas do kernel devem possuir a mesma largura."
                );
            }

            System.arraycopy(values[y], 0, this.values[y], 0, width);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffset() {
        return offset;
    }

    public double get(int x, int y) {
        return values[y][x];
    }

    public double getValue(int x, int y) {
        return values[y][x];
    }

    public double[][] getValues() {
        double[][] copy = new double[height][width];

        for (int y = 0; y < height; y++) {
            System.arraycopy(values[y], 0, copy[y], 0, width);
        }

        return copy;
    }

    /**
     * Calcula a soma dos coeficientes da máscara.
     */
    public double getSum() {
        double sum = 0.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += values[y][x];
            }
        }

        return sum;
    }
}