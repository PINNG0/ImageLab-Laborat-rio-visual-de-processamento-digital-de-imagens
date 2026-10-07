package imagelab.ui;

import imagelab.controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Interface principal do ImageLab.
 *
 * Organiza a aplicação em um cabeçalho compacto, área de operação e
 * configuração, comparação das imagens e barra de status.
 */
public class MainView extends BorderPane {

    private final ImageViewPane original =
            new ImageViewPane("Original");

    private final ImageViewPane result =
            new ImageViewPane("Resultado atual");

    private final AnalysisPane analysis =
            new AnalysisPane();

    private final VBox parameterBox =
            new VBox(6);

    private final ComboBox<String> operation =
            new ComboBox<>();

    private final Spinner<Integer> neighborhood =
            new Spinner<>();

    private final Label neighborhoodLabel =
            new Label("Vizinhança");

    private final Label category =
            new Label("Transformação de intensidade");

    private final Label status =
            new Label("Pronto");

    private final Label parameterHint =
            new Label("Escolha uma operação para configurar.");

    private final ScrollPane parameterScroll =
            new ScrollPane(parameterBox);

    private final MainController controller;

    public MainView(Stage stage) {

        getStyleClass().add("root-pane");

        Label title =
                new Label(
                        "ImageLab — Laboratório visual de processamento digital de imagens · AV1"
                );

        title.getStyleClass().add("main-title");

        Button openButton =
                button("Abrir imagem");

        Button saveButton =
                button("Salvar resultado");

        Button undoButton =
                button("Desfazer");

        Button resetButton =
                button("Restaurar");

        Button applyButton =
                button("Aplicar");

        applyButton.getStyleClass().add(
                "primary-button"
        );

        operation.getItems().setAll(
                OperationCatalog.operations()
        );

        operation.getSelectionModel().selectFirst();

        operation.setPrefWidth(310);

        operation.setMinWidth(240);

        operation.setMaxWidth(
                Double.MAX_VALUE
        );

        Label operationLabel =
                new Label("Operação");

        operationLabel.getStyleClass().add(
                "toolbar-label"
        );

        category.getStyleClass().add(
                "category-badge"
        );

        neighborhood.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        15,
                        3,
                        2
                )
        );

        neighborhood.setPrefWidth(90);

        neighborhood.setEditable(true);

        neighborhoodLabel.getStyleClass().add(
                "toolbar-label"
        );

        HBox actions =
                new HBox(
                        8,
                        openButton,
                        saveButton,
                        undoButton,
                        resetButton
                );

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        actions.getStyleClass().add(
                "action-row"
        );

        HBox operationBox =
                new HBox(
                        8,
                        operationLabel,
                        operation,
                        category
                );

        operationBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                operation,
                Priority.ALWAYS
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox neighborhoodBox =
                new HBox(
                        8,
                        neighborhoodLabel,
                        neighborhood
                );

        neighborhoodBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox operationRow =
                new HBox(
                        12,
                        operationBox,
                        spacer,
                        neighborhoodBox,
                        applyButton
                );

        operationRow.setAlignment(
                Pos.CENTER_LEFT
        );

        operationRow.getStyleClass().add(
                "control-row"
        );

        parameterBox.setPadding(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        parameterBox.getStyleClass().add(
                "parameter-area"
        );

        parameterScroll.setFitToWidth(true);

        parameterScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        parameterScroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        parameterScroll.setPrefViewportHeight(
                240
        );

        parameterScroll.setMinViewportHeight(
                170
        );

        parameterScroll.getStyleClass().add(
                "parameter-scroll"
        );

        parameterHint.getStyleClass().add(
                "parameter-hint"
        );

        Label parameterTitle =
                new Label("Parâmetros");

        parameterTitle.getStyleClass().add(
                "card-title"
        );

        HBox parameterHeader =
                new HBox(
                        8,
                        parameterTitle,
                        parameterHint
                );

        parameterHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                parameterHint,
                Priority.ALWAYS
        );

        VBox parameterCard =
                new VBox(
                        7,
                        parameterHeader,
                        parameterScroll
                );

        parameterCard.setMinWidth(0);
        parameterCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(parameterScroll, Priority.ALWAYS);

        parameterCard.getStyleClass().add(
                "parameter-card-wrap"
        );

        HBox titleRow =
                new HBox(
                        12,
                        title,
                        actions
                );

        titleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                title,
                Priority.ALWAYS
        );

        VBox header =
                new VBox(
                        9,
                        titleRow,
                        operationRow
                );

        header.setPadding(
                new Insets(
                        10,
                        16,
                        8,
                        16
                )
        );

        header.getStyleClass().add(
                "header"
        );

        VBox imageSection =
                new VBox(
                        comparisonArea()
                );

        imageSection.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        imageSection.getStyleClass().add(
                "image-section"
        );

        // Mantém os painéis de imagem compactos e iguais, reservando o
        // restante da altura da janela para o modo de análise.
        imageSection.setMinHeight(0);
        imageSection.setPrefHeight(410);
        imageSection.setMaxHeight(410);

        ScrollPane analysisScroll =
                new ScrollPane(analysis);

        analysisScroll.setFitToWidth(true);
        analysisScroll.setFitToHeight(true);

        analysisScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        analysisScroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        analysisScroll.setPrefWidth(360);
        analysisScroll.setMinWidth(330);
        analysisScroll.setMaxWidth(390);

        analysisScroll.getStyleClass().add(
                "analysis-scroll"
        );

        VBox leftColumn =
                new VBox(
                        8,
                        parameterCard,
                        imageSection
                );

        VBox.setVgrow(
                parameterCard,
                Priority.ALWAYS
        );

        leftColumn.setMinWidth(0);
        leftColumn.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(
                leftColumn,
                Priority.ALWAYS
        );

        HBox workspace =
                new HBox(
                        10,
                        leftColumn,
                        analysisScroll
                );

        workspace.setFillHeight(true);
        workspace.setMinHeight(0);
        workspace.setMaxHeight(Double.MAX_VALUE);

        workspace.getStyleClass().add(
                "configuration-area"
        );

        status.getStyleClass().add(
                "status-bar"
        );

        status.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                status,
                Priority.ALWAYS
        );

        Label shortcut =
                new Label(
                        "Ctrl+O abrir  ·  Ctrl+S salvar  ·  "
                                + "Ctrl+Z desfazer  ·  Enter aplicar  ·  "
                                + "Ctrl+roda zoom"
                );

        shortcut.getStyleClass().add(
                "shortcut-hint"
        );

        HBox footer =
                new HBox(
                        status,
                        shortcut
                );

        footer.setSpacing(12);

        footer.setPadding(
                new Insets(
                        6,
                        14,
                        6,
                        14
                )
        );

        footer.getStyleClass().add(
                "footer"
        );

        setTop(header);

        setCenter(workspace);

        setBottom(footer);

        controller =
                new MainController(
                        original,
                        result,
                        analysis,
                        parameterBox
                );

        controller.setStatusHandler(
                status::setText
        );

        // O modo de análise também permite inspecionar pixels diretamente.
        original.setPixelHandler((x, y) -> {
            if (controller.getCurrentImage() != null) {
                analysis.setPixelInfo(
                        "Original",
                        x,
                        y,
                        controller.getCurrentImage().get(x, y)
                );
            }
        });

        result.setPixelHandler((x, y) -> {
            if (controller.getCurrentResult() != null) {
                analysis.setPixelInfo(
                        "Resultado",
                        x,
                        y,
                        controller.getCurrentResult().get(x, y)
                );
            }
        });

        operation.valueProperty().addListener(
                (obs, oldValue, newValue) -> {

                    updateContext(newValue);

                    controller.updateParameters(
                            newValue,
                            getWindow()
                    );
                }
        );

        openButton.setOnAction(
                e -> controller.openImage(
                        getWindow()
                )
        );

        saveButton.setOnAction(
                e -> controller.saveResult(
                        getWindow()
                )
        );

        undoButton.setOnAction(
                e -> controller.undoLastOperation()
        );

        resetButton.setOnAction(
                e -> controller.resetResult()
        );

        applyButton.setOnAction(
                e -> {

                    try {

                        controller.applyOperation(
                                operation.getValue(),
                                safeNeighborhood()
                        );

                    } catch (IllegalArgumentException ex) {

                        status.setText(
                                ex.getMessage()
                        );
                    }
                }
        );

        addEventFilter(
                KeyEvent.KEY_PRESSED,
                e -> handleShortcut(
                        e,
                        applyButton
                )
        );

        controller.updateParameters(
                operation.getValue(),
                stage
        );

        updateContext(
                operation.getValue()
        );

        analysis.setText(
                "Abra uma imagem para começar.\n\n"
                        + "O ImageLab converte a entrada para "
                        + "escala de cinza de 8 bits.\n\n"
                        + "Fluxo: abrir → escolher operação → "
                        + "ajustar parâmetros → aplicar."
        );
    }

    /**
     * Cria as duas áreas de imagem em colunas independentes.
     *
     * Cada imagem possui sua própria viewport, portanto o zoom de uma
     * nunca deve alterar o espaço disponível para a outra.
     */
    private GridPane comparisonArea() {

        GridPane area =
                new GridPane();

        area.setHgap(14);

        area.setVgap(0);

        area.setMaxWidth(
                Double.MAX_VALUE
        );

        area.setMaxHeight(
                Double.MAX_VALUE
        );

        area.getStyleClass().add(
                "comparison-area"
        );

        ColumnConstraints left =
                new ColumnConstraints();

        left.setPercentWidth(50);

        left.setHgrow(
                Priority.ALWAYS
        );

        ColumnConstraints right =
                new ColumnConstraints();

        right.setPercentWidth(50);

        right.setHgrow(
                Priority.ALWAYS
        );

        area.getColumnConstraints().setAll(
                left,
                right
        );

        original.setMinWidth(0);
        result.setMinWidth(0);

        original.setPrefHeight(394);
        result.setPrefHeight(394);
        original.setMinHeight(394);
        result.setMinHeight(394);
        original.setMaxHeight(394);
        result.setMaxHeight(394);

        original.setMaxWidth(Double.MAX_VALUE);
        result.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(
                original,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                result,
                Priority.ALWAYS
        );

        GridPane.setVgrow(
                original,
                Priority.ALWAYS
        );

        GridPane.setVgrow(
                result,
                Priority.ALWAYS
        );

        area.add(
                original,
                0,
                0
        );

        area.add(
                result,
                1,
                0
        );

        return area;
    }

    private Button button(String text) {

        Button button =
                new Button(text);

        button.setFocusTraversable(
                false
        );

        return button;
    }

    /**
     * Atualiza categoria e estado da vizinhança.
     */
    private void updateContext(
            String selected
    ) {

        category.setText(
                OperationCatalog.category(
                        selected
                )
        );

        boolean usesNeighborhood =
                selected != null
                        && (
                        selected.equals("Filtro da média")
                                || selected.equals("Filtro da mediana")
                                || selected.equals("Contraste adaptativo")
                );

        neighborhoodLabel.setDisable(
                !usesNeighborhood
        );

        neighborhood.setDisable(
                !usesNeighborhood
        );
    }

    /**
     * Lê a vizinhança garantindo que ela seja ímpar.
     */
    private int safeNeighborhood() {

        try {

            int value =
                    neighborhood
                            .getValueFactory()
                            .getValue();

            if (value < 1) {
                return 1;
            }

            if (value % 2 == 0) {
                value--;
            }

            return value;

        } catch (Exception e) {

            return 3;
        }
    }

    /**
     * Atalhos globais da aplicação.
     */
    private void handleShortcut(
            KeyEvent event,
            Button applyButton
    ) {

        if (event.isControlDown()
                && event.getCode() == KeyCode.O) {

            controller.openImage(
                    getWindow()
            );

            event.consume();

        } else if (
                event.isControlDown()
                        && event.getCode() == KeyCode.S
        ) {

            controller.saveResult(
                    getWindow()
            );

            event.consume();

        } else if (
                event.isControlDown()
                        && event.getCode() == KeyCode.Z
        ) {

            controller.undoLastOperation();

            event.consume();

        } else if (
                event.getCode() == KeyCode.ENTER
                        && !event.isControlDown()
                        && !event.isShiftDown()
        ) {

            applyButton.fire();

            event.consume();
        }
    }

    /**
     * Obtém a janela atual com segurança.
     */
    private Window getWindow() {

        if (getScene() == null) {
            return null;
        }

        return getScene().getWindow();
    }
}