package imagelab.util;

/**
 * Reúne operações auxiliares relacionadas aos valores dos pixels.
 *
 * Como a imagem trabalha com 8 bits em escala de cinza,
 * os valores finais devem permanecer entre 0 e 255.
 */
public final class PixelUtils {

    private PixelUtils() {
    }

    /**
     * Limita um valor inteiro ao intervalo [0, 255].
     *
     * Valores menores que 0 tornam-se 0.
     * Valores maiores que 255 tornam-se 255.
     */
    public static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Arredonda um valor real e depois limita o resultado
     * ao intervalo permitido para um pixel de 8 bits.
     */
    public static int clamp(double value) {
        return clamp((int) Math.round(value));
    }

    /**
     * Converte um valor real para um valor de pixel válido.
     */
    public static int normalize(double value) {
        if (value <= 0) {
            return 0;
        }

        if (value >= 255) {
            return 255;
        }

        return (int) Math.round(value);
    }

    /**
     * Calcula o negativo de um pixel.
     *
     * A transformação é dada por:
     *
     * s = 255 - r
     */
    public static int invert(int value) {
        return 255 - clamp(value);
    }
}