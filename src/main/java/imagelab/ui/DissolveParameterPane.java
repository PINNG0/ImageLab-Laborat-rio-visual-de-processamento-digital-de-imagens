package imagelab.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controles visuais específicos das operações de dissolve.
 *
 * Não executa processamento: apenas fornece os parâmetros escolhidos
 * pelo usuário e eventos para o controller.
 */
public class DissolveParameterPane extends VBox {

    private final boolean nonUniform;
    private final Button secondImageButton =
            new Button("Selecionar 2ª imagem");
    private final Button maskButton =
            new Button("Selecionar máscara");
    private final Label secondImageLabel =
            new Label("Nenhuma imagem selecionada");
    private final Label maskLabel =
            new Label("Nenhuma máscara selecionada");
    private final Spinner<Double> alphaSpinner = new Spinner<>();

    public DissolveParameterPane(boolean nonUniform) {
        this.nonUniform = nonUniform;

        secondImageButton.getStyleClass().add("secondary-action");
        maskButton.getStyleClass().add("secondary-action");
        secondImageLabel.getStyleClass().add("parameter-help");
        maskLabel.getStyleClass().add("parameter-help");

        setSpacing(8);
        setPadding(new Insets(8));

        Label title = new Label(
                nonUniform
                        ? "Dissolve não uniforme"
                        : "Dissolve uniforme"
        );
        title.getStyleClass().add("section-title");

        Label description = new Label(
                nonUniform
                        ? "A máscara define, pixel a pixel, o peso das duas imagens."
                        : "Combina duas imagens usando um único valor de alpha."
        );
        description.setWrapText(true);

        HBox secondBox = new HBox(
                8,
                secondImageButton,
                secondImageLabel
        );

        getChildren().addAll(
                title,
                description,
                secondBox
        );

        if (nonUniform) {
            maskLabel.setWrapText(true);

            HBox maskBox = new HBox(
                    8,
                    maskButton,
                    maskLabel
            );

            Label info = new Label(
                    "Preto (0) mantém a imagem original; "
                            + "branco (255) favorece a 2ª imagem."
            );
            info.setWrapText(true);

            getChildren().addAll(maskBox, info);
        } else {
            alphaSpinner.setValueFactory(
                    new SpinnerValueFactory.DoubleSpinnerValueFactory(
                            0.0, 1.0, 0.5, 0.05
                    )
            );
            alphaSpinner.setEditable(true);
            alphaSpinner.setPrefWidth(100);

            getChildren().add(
                    new HBox(
                            8,
                            new Label("Alpha da 2ª imagem:"),
                            alphaSpinner
                    )
            );
        }
    }

    public void setOnSecondImage(Runnable action) {
        secondImageButton.setOnAction(event -> action.run());
    }

    public void setOnMask(Runnable action) {
        maskButton.setOnAction(event -> action.run());
    }

    public double getAlpha() {
        try {
            return Double.parseDouble(
                    alphaSpinner.getEditor()
                            .getText()
                            .trim()
                            .replace(',', '.')
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Alpha inválido. Use um valor entre 0 e 1."
            );
        }
    }

    public void setSecondImageInfo(String text) {
        secondImageLabel.setText(text);
    }

    public void setMaskInfo(String text) {
        maskLabel.setText(text);
    }

    public boolean isNonUniform() {
        return nonUniform;
    }
}
