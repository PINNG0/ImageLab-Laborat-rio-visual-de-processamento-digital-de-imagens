package imagelab.ui;

import imagelab.model.Kernel;
import imagelab.processing.ConvolutionPresets;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Editor visual de kernels de convolução.
 *
 * A classe cuida apenas da edição/apresentação da máscara.
 * Os algoritmos e os presets ficam no pacote processing.
 */
public class KernelEditorPane extends VBox {

    private final Spinner<Integer> rowsSpinner = new Spinner<>();
    private final Spinner<Integer> columnsSpinner = new Spinner<>();
    private final ComboBox<String> presetCombo = new ComboBox<>();
    private final TextField offsetField = new TextField("0");
    private final GridPane grid = new GridPane();
    private final ScrollPane gridScroll = new ScrollPane(grid);

    private final Map<String, Kernel> presets = ConvolutionPresets.all();
    private TextField[][] cells;

    private boolean rebuilding;

    public KernelEditorPane() {
        setSpacing(8);
        setPadding(new Insets(8));

        configureSpinners();
        configurePresetSelector();
        configureGrid();

        HBox sizeBox = new HBox(
                8,
                new Label("Linhas:"),
                rowsSpinner,
                new Label("Colunas:"),
                columnsSpinner
        );

        HBox offsetBox = new HBox(
                8,
                new Label("Offset:"),
                offsetField
        );

        HBox presetBox = new HBox(
                8,
                new Label("Preset:"),
                presetCombo
        );

        Label title = new Label("Máscara da convolução");
        title.getStyleClass().add("section-title");

        getChildren().addAll(
                title,
                presetBox,
                sizeBox,
                gridScroll,
                offsetBox
        );

        rebuild();
    }

    private void configureSpinners() {
        rowsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, 15, 3
                )
        );

        columnsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, 15, 3
                )
        );

        rowsSpinner.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (!rebuilding) {
                        presetCombo.getSelectionModel().select("Personalizada");
                        rebuild();
                    }
                }
        );

        columnsSpinner.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (!rebuilding) {
                        presetCombo.getSelectionModel().select("Personalizada");
                        rebuild();
                    }
                }
        );
    }

    private void configurePresetSelector() {
        presetCombo.getItems().setAll(presets.keySet());
        presetCombo.getSelectionModel().select("Personalizada");

        presetCombo.valueProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals("Personalizada")) {
                        setKernel(presets.get(newValue));
                    }
                }
        );
    }

    private void configureGrid() {
        grid.setHgap(5);
        grid.setVgap(5);

        gridScroll.setFitToWidth(true);
        gridScroll.setPrefViewportHeight(110);
        gridScroll.setMinViewportHeight(80);
        gridScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gridScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gridScroll.setMaxWidth(Double.MAX_VALUE);
    }

    private void rebuild() {
        rebuilding = true;

        int rows = rowsSpinner.getValue();
        int columns = columnsSpinner.getValue();

        grid.getChildren().clear();
        cells = new TextField[rows][columns];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                TextField field = new TextField("0");
                field.setPrefWidth(60);
                cells[y][x] = field;
                grid.add(field, x, y);
            }
        }

        if (rows == 3 && columns == 3) {
            cells[1][1].setText("1");
        }

        rebuilding = false;
    }

    /**
     * Preenche o editor com um kernel existente.
     */
    public void setKernel(Kernel kernel) {
        if (kernel == null) {
            throw new IllegalArgumentException("O kernel não pode ser nulo.");
        }

        rebuilding = true;

        rowsSpinner.getValueFactory().setValue(kernel.getHeight());
        columnsSpinner.getValueFactory().setValue(kernel.getWidth());

        rebuilding = false;
        rebuild();

        for (int y = 0; y < kernel.getHeight(); y++) {
            for (int x = 0; x < kernel.getWidth(); x++) {
                cells[y][x].setText(
                        formatNumber(kernel.getValue(x, y))
                );
            }
        }

        offsetField.setText(Integer.toString(kernel.getOffset()));
    }

    public Kernel getKernel() {
        double[][] values = new double[cells.length][cells[0].length];

        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                values[y][x] = parseDouble(
                        cells[y][x].getText(),
                        "coeficiente da máscara"
                );
            }
        }

        int offset = (int) Math.round(
                parseDouble(offsetField.getText(), "offset")
        );

        return new Kernel(values, offset);
    }

    public String getDescription() {
        StringBuilder text = new StringBuilder();

        for (TextField[] row : cells) {
            for (int x = 0; x < row.length; x++) {
                if (x > 0) {
                    text.append("  ");
                }
                text.append(row[x].getText());
            }
            text.append('\n');
        }

        text.append("Offset: ").append(offsetField.getText());
        return text.toString();
    }

    private static double parseDouble(String text, String name) {
        try {
            return Double.parseDouble(
                    text.trim().replace(',', '.')
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para " + name + ": " + text
            );
        }
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
}
