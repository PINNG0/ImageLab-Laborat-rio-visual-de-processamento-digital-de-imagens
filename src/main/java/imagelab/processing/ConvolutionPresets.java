package imagelab.processing;

import imagelab.model.Kernel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Máscaras de convolução exigidas pela AV1.
 *
 * Mantém os presets fora do controller para que a lógica da interface
 * não fique misturada com os dados dos algoritmos.
 */
public final class ConvolutionPresets {

    private ConvolutionPresets() {
    }

    public static Map<String, Kernel> all() {
        Map<String, Kernel> presets = new LinkedHashMap<>();

        presets.put("Personalizada", new Kernel(new double[][]{
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        }));

        presets.put("Aguçamento — c=1, d=1",
                sharpening(1, 1));

        presets.put("Aguçamento — c=2, d=2",
                sharpening(2, 2));

        presets.put("Aguçamento — c=1, d=2",
                sharpening(1, 2));

        presets.put("Relevo — h1", new Kernel(new double[][]{
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, -1}
        }));

        presets.put("Relevo — h2", new Kernel(new double[][]{
                {0, 0, 2},
                {0, -1, 0},
                {-1, 0, 0}
        }));

        presets.put("Bordas — h3", new Kernel(new double[][]{
                {-1, -1, 0},
                {1, 0, 1},
                {0, 1, 1}
        }));

        presets.put("Bordas — h4", new Kernel(new double[][]{
                {0, -1, 0},
                {-1, -4, -1},
                {0, -1, 0}
        }));

        return presets;
    }

    public static Kernel sharpening(int c, int d) {
        if (c <= 0 || d <= 0) {
            throw new IllegalArgumentException(
                    "c e d devem ser positivos."
            );
        }

        return new Kernel(new double[][]{
                {-c, -c, -c},
                {-c, 8.0 * c + d, -c},
                {-c, -c, -c}
        });
    }
}
