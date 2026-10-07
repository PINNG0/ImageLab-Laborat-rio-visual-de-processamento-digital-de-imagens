package imagelab.processing;

import imagelab.model.GrayImage;
import imagelab.model.Kernel;
import imagelab.model.ProcessingResult;
import imagelab.util.BorderHandler;
import imagelab.util.PixelUtils;

/**
 * Implementa aguçamento por Laplaciano e high boost.
 *
 * Mantém as respostas intermediárias necessárias para análise das operações.
 */
public final class Sharpening {

    private Sharpening() {
    }

    /**
     * Mantém a chamada simplificada do aguçamento com c=d=1.
     * O parâmetro booleano é preservado apenas por compatibilidade.
     */
    public static ProcessingResult laplacian(GrayImage image, boolean subtract) {
        return laplacian(image, 1.0, 1.0);
    }

    /**
     * Aplica a máscara obrigatória de aguçamento:
     * [-c -c -c; -c 8c+d -c; -c -c -c].
     * A resposta do Laplaciano de 8 vizinhos é mostrada separadamente.
     */
    public static ProcessingResult laplacian(GrayImage image, double c, double d) {
        if (image == null) {
            throw new IllegalArgumentException("A imagem não pode ser nula.");
        }
        if (c <= 0 || d <= 0) {
            throw new IllegalArgumentException("c e d devem ser positivos.");
        }

        Kernel laplacianKernel = new Kernel(new double[][]{
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1}
        });
        double[][] response = signedConvolution(image, laplacianKernel);

        GrayImage laplacian = new GrayImage(image.getWidth(), image.getHeight());
        GrayImage result = new GrayImage(image.getWidth(), image.getHeight());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double lap = response[y][x];
                laplacian.set(x, y, PixelUtils.clamp(Math.abs(lap)));
                double value = d * image.get(x, y) + c * lap;
                result.set(x, y, PixelUtils.clamp(value));
            }
        }

        ProcessingResult output = new ProcessingResult(result);
        output.addIntermediate("Laplaciano", laplacian);
        return output;
    }

    /**
     * Aplica high boost com A > 1 e média 3×3 como imagem suavizada.
     * A resposta final e as imagens intermediárias são mantidas no resultado.
     */
    public static ProcessingResult highBoostResult(GrayImage image, double a) {
        if (a <= 1.0) {
            throw new IllegalArgumentException("O fator A do high boost deve ser maior que 1.");
        }

        GrayImage blurred = SpatialFilters.mean(image, 3);
        GrayImage details = new GrayImage(image.getWidth(), image.getHeight());
        GrayImage result = new GrayImage(image.getWidth(), image.getHeight());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double detail = image.get(x, y) - blurred.get(x, y);
                details.set(x, y, PixelUtils.clamp(Math.abs(detail)));
                double value = a * image.get(x, y) - blurred.get(x, y);
                result.set(x, y, PixelUtils.clamp(value));
            }
        }

        ProcessingResult output = new ProcessingResult(result);
        output.addIntermediate("Imagem suavizada", blurred);
        output.addIntermediate("Detalhes (|f − suavizada|)", details);
        return output;
    }

    public static GrayImage highBoost(GrayImage image, int c, double amount) {
        return highBoost(image, (double) c, amount);
    }

    public static GrayImage highBoost(GrayImage image, double c, double amount) {
        if (c <= 0) {
            throw new IllegalArgumentException("c deve ser positivo.");
        }
        GrayImage blurred = SpatialFilters.mean(image, 3);
        GrayImage result = new GrayImage(image.getWidth(), image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double mask = image.get(x, y) - blurred.get(x, y);
                double value = c * image.get(x, y) + amount * mask;
                result.set(x, y, PixelUtils.clamp(value));
            }
        }
        return result;
    }

    private static double[][] signedConvolution(GrayImage image, Kernel kernel) {
        double[][] result = new double[image.getHeight()][image.getWidth()];
        int centerX = kernel.getWidth() / 2;
        int centerY = kernel.getHeight() / 2;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double sum = 0;
                for (int ky = 0; ky < kernel.getHeight(); ky++) {
                    for (int kx = 0; kx < kernel.getWidth(); kx++) {
                        int imageX = x + kx - centerX;
                        int imageY = y + ky - centerY;
                        int pixel = BorderHandler.getReplicated(image, imageX, imageY);
                        sum += pixel * kernel.getValue(kx, ky);
                    }
                }
                result[y][x] = sum;
            }
        }
        return result;
    }
}
