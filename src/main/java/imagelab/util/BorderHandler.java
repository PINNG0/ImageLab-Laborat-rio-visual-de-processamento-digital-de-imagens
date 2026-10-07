package imagelab.util;

import imagelab.model.GrayImage;

/**
 * Responsável pelo tratamento das bordas durante operações
 * baseadas em vizinhança.
 *
 * Quando uma operação tenta acessar uma posição fora da imagem,
 * a estratégia utilizada aqui é a replicação da borda.
 *
 * Exemplo:
 *
 * [10 20 30]
 *
 * Ao solicitar uma posição x = -1, utiliza-se o primeiro pixel:
 * 10
 *
 * Ao solicitar uma posição além do último pixel, utiliza-se
 * o último pixel disponível.
 */
public final class BorderHandler {

    private BorderHandler() {
    }

    /**
     * Obtém um pixel de uma matriz utilizando replicação das bordas.
     *
     * Caso x ou y esteja fora dos limites, a coordenada é ajustada
     * para a posição válida mais próxima.
     */
    public static int getReplicated(
            int[][] pixels,
            int x,
            int y
    ) {
        if (pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            throw new IllegalArgumentException(
                    "A matriz de pixels não pode ser vazia."
            );
        }

        int correctedY = Math.max(
                0,
                Math.min(y, pixels.length - 1)
        );

        int correctedX = Math.max(
                0,
                Math.min(x, pixels[0].length - 1)
        );

        return pixels[correctedY][correctedX];
    }

    /**
     * Obtém um pixel de uma GrayImage utilizando replicação das bordas.
     */
    public static int getReplicated(
            GrayImage image,
            int x,
            int y
    ) {
        if (image == null) {
            throw new IllegalArgumentException(
                    "A imagem não pode ser nula."
            );
        }

        int correctedX = Math.max(
                0,
                Math.min(x, image.getWidth() - 1)
        );

        int correctedY = Math.max(
                0,
                Math.min(y, image.getHeight() - 1)
        );

        return image.getPixel(correctedX, correctedY);
    }
}