package imagelab.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resultado de um processamento.
 * Pode armazenar a imagem final e imagens/respostas intermediárias.
 */
public class ProcessingResult {

    private final GrayImage image;
    private final Map<String, GrayImage> intermediates = new LinkedHashMap<>();

    public ProcessingResult(GrayImage image) {
        this.image = image;
    }

    public GrayImage getImage() {
        return image;
    }

    public void addIntermediate(String name, GrayImage image) {
        intermediates.put(name, image);
    }

    public Map<String, GrayImage> getIntermediates() {
        return Collections.unmodifiableMap(intermediates);
    }
}
