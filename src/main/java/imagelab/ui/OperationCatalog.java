package imagelab.ui;

import java.util.List;
import java.util.Map;

/**
 * Centraliza nomes, categorias e descrições das operações exibidas na interface.
 * Isso evita duplicar listas e textos entre a tela principal e o controller.
 */
public final class OperationCatalog {

    private static final List<String> OPERATIONS = List.of(
            "Negativo",
            "Limiarização",
            "Alargamento de contraste",
            "Gamma",
            "Logaritmo",
            "Histograma",
            "Expansão de histograma",
            "Equalização",
            "Filtro da média",
            "Filtro da mediana",
            "Roberts",
            "Sobel",
            "Kirsch",
            "Laplaciano",
            "Aguçamento",
            "High boost",
            "Convolução genérica",
            "Contraste adaptativo",
            "Dissolve uniforme",
            "Dissolve não uniforme"
    );

    private static final Map<String, String> CATEGORIES = Map.ofEntries(
            Map.entry("Negativo", "Transformação de intensidade"),
            Map.entry("Limiarização", "Transformação de intensidade"),
            Map.entry("Alargamento de contraste", "Transformação de intensidade"),
            Map.entry("Gamma", "Transformação de intensidade"),
            Map.entry("Logaritmo", "Transformação de intensidade"),
            Map.entry("Histograma", "Histograma"),
            Map.entry("Expansão de histograma", "Histograma"),
            Map.entry("Equalização", "Histograma"),
            Map.entry("Filtro da média", "Filtragem espacial"),
            Map.entry("Filtro da mediana", "Filtragem espacial"),
            Map.entry("Roberts", "Detecção de bordas"),
            Map.entry("Sobel", "Detecção de bordas"),
            Map.entry("Kirsch", "Detecção de bordas"),
            Map.entry("Laplaciano", "Detecção de bordas"),
            Map.entry("Aguçamento", "Aguçamento"),
            Map.entry("High boost", "Aguçamento"),
            Map.entry("Convolução genérica", "Convolução"),
            Map.entry("Contraste adaptativo", "Contraste adaptativo"),
            Map.entry("Dissolve uniforme", "Operações algébricas"),
            Map.entry("Dissolve não uniforme", "Operações algébricas")
    );

    private OperationCatalog() {
    }

    public static List<String> operations() {
        return OPERATIONS;
    }

    public static String category(String operation) {
        return CATEGORIES.getOrDefault(operation, "Operação");
    }
}
