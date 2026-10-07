package imagelab.ui;

import imagelab.model.GrayImage;
import imagelab.util.ImageIO;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exibe os dados técnicos da operação atual, incluindo parâmetros,
 * máscaras, histogramas, respostas intermediárias e o pixel sob o cursor.
 */
public class AnalysisPane extends VBox {

    private final TextArea information = new TextArea();
    private final Label histogramTitle = new Label("Histograma");
    private final Canvas histogramCanvas = new Canvas(290, 118);
    private final Label maskTitle = new Label("Máscara / operador");
    private final Label maskText = new Label();
    private final Label pixelTitle = new Label("Pixel sob o cursor");
    private final Label pixelText = new Label("Mova o cursor sobre uma imagem.");
    private final Label intermediateTitle = new Label("Resposta intermediária");
    private final ComboBox<String> intermediateSelector = new ComboBox<>();
    private final ImageView intermediateView = new ImageView();
    private final StackPane intermediateContainer = new StackPane();
    private final Map<String, GrayImage> intermediateImages = new LinkedHashMap<>();

    public AnalysisPane() {
        Label title = new Label("Modo de análise");
        title.getStyleClass().add("analysis-title");

        information.setEditable(false);
        information.setWrapText(true);
        information.setPrefRowCount(10);
        information.setPrefHeight(260);
        information.setMinHeight(220);
        information.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(information, javafx.scene.layout.Priority.ALWAYS);
        information.getStyleClass().add("analysis-text");

        histogramTitle.getStyleClass().add("analysis-subtitle");
        histogramCanvas.setVisible(false);
        histogramCanvas.setManaged(false);
        histogramCanvas.getStyleClass().add("histogram-canvas");

        maskTitle.getStyleClass().add("analysis-subtitle");
        maskText.getStyleClass().add("analysis-detail");
        maskText.setWrapText(true);
        maskTitle.setVisible(false);
        maskTitle.setManaged(false);
        maskText.setVisible(false);
        maskText.setManaged(false);

        pixelTitle.getStyleClass().add("analysis-subtitle");
        pixelText.getStyleClass().add("analysis-detail");
        pixelText.setWrapText(true);

        intermediateTitle.getStyleClass().add("analysis-subtitle");
        intermediateSelector.setMaxWidth(Double.MAX_VALUE);
        intermediateSelector.setVisible(false);
        intermediateSelector.setManaged(false);
        intermediateTitle.setVisible(false);
        intermediateTitle.setManaged(false);

        intermediateView.setPreserveRatio(true);
        intermediateView.setFitWidth(270);
        intermediateView.setFitHeight(185);
        intermediateView.setVisible(false);
        intermediateView.setManaged(false);

        intermediateContainer.setMinHeight(0);
        intermediateContainer.setPrefHeight(190);
        intermediateContainer.getStyleClass().add("intermediate-viewer");
        intermediateContainer.getChildren().add(intermediateView);
        intermediateContainer.setVisible(false);
        intermediateContainer.setManaged(false);

        intermediateSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            GrayImage image = intermediateImages.get(newValue);
            intermediateView.setImage(image == null ? null : ImageIO.toFxImage(image));
        });

        getChildren().addAll(
                title,
                information,
                histogramTitle,
                histogramCanvas,
                maskTitle,
                maskText,
                pixelTitle,
                pixelText,
                intermediateTitle,
                intermediateSelector,
                intermediateContainer
        );

        histogramTitle.setVisible(false);
        histogramTitle.setManaged(false);
        setSpacing(9);
        setPrefWidth(315);
        setMinWidth(290);
        getStyleClass().add("analysis-pane");
    }

    public void setText(String text) {
        information.setText(text == null ? "" : text);
        information.positionCaret(0);
    }

    public void clear() {
        information.clear();
        setHistogram(null);
        setMaskDescription(null);
        setIntermediates(null);
    }

    /** Mostra um histograma compacto com os 256 níveis de intensidade. */
    public void setHistogram(int[] histogram) {
        boolean visible = histogram != null && histogram.length == 256;
        histogramTitle.setVisible(visible);
        histogramTitle.setManaged(visible);
        histogramCanvas.setVisible(visible);
        histogramCanvas.setManaged(visible);
        if (!visible) {
            return;
        }

        GraphicsContext gc = histogramCanvas.getGraphicsContext2D();
        double width = histogramCanvas.getWidth();
        double height = histogramCanvas.getHeight();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.web("#f8fafc"));
        gc.fillRect(0, 0, width, height);

        double max = 1;
        for (int value : histogram) {
            max = Math.max(max, value);
        }

        gc.setStroke(Color.web("#cbd5e1"));
        gc.strokeLine(0, height - 1, width, height - 1);
        gc.setFill(Color.web("#2563eb"));

        double barWidth = width / 256.0;
        for (int i = 0; i < 256; i++) {
            double barHeight = histogram[i] * (height - 8) / max;
            gc.fillRect(i * barWidth, height - barHeight - 1,
                    Math.max(1, barWidth), barHeight);
        }
    }

    /** Exibe a máscara ou o operador usado pela operação atual. */
    public void setMaskDescription(String description) {
        boolean visible = description != null && !description.isBlank();
        maskTitle.setVisible(visible);
        maskTitle.setManaged(visible);
        maskText.setVisible(visible);
        maskText.setManaged(visible);
        maskText.setText(visible ? description : "");
    }

    /** Atualiza a leitura do pixel sob o cursor. */
    public void setPixelInfo(String source, int x, int y, int value) {
        pixelText.setText(
                source + "  ·  x=" + x + ", y=" + y + "  ·  intensidade=" + value
        );
    }

    /** Mostra as respostas intermediárias produzidas pelo processamento. */
    public void setIntermediates(Map<String, GrayImage> intermediates) {
        intermediateImages.clear();
        intermediateSelector.getItems().clear();

        boolean visible = intermediates != null && !intermediates.isEmpty();
        intermediateTitle.setVisible(visible);
        intermediateTitle.setManaged(visible);
        intermediateSelector.setVisible(visible);
        intermediateSelector.setManaged(visible);
        intermediateContainer.setVisible(visible);
        intermediateContainer.setManaged(visible);
        intermediateView.setVisible(visible);
        intermediateView.setManaged(visible);

        if (!visible) {
            intermediateView.setImage(null);
            return;
        }

        intermediateImages.putAll(intermediates);
        intermediateSelector.getItems().setAll(intermediateImages.keySet());
        intermediateSelector.getSelectionModel().selectFirst();
    }
}
