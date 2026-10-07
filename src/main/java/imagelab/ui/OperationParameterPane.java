package imagelab.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Painel de parâmetros das transformações que precisam de valores editáveis.
 *
 * A classe apenas apresenta e lê os campos; o processamento continua no pacote
 * processing e a decisão da operação fica no controller.
 */
public class OperationParameterPane extends VBox {

    private final TextField threshold = field("128");
    private final TextField gamma = field("0.5");
    private final TextField gammaC = field("1.0");
    private final TextField logC = field("45.99");

    private final TextField r1 = field("50");
    private final TextField s1 = field("0");
    private final TextField r2 = field("200");
    private final TextField s2 = field("255");

    private final TextField adaptiveC = field("1.5");
    private final TextField sharpenC = field("1");
    private final TextField sharpenD = field("1");
    private final TextField highBoostA = field("2.0");

    public OperationParameterPane(String operation) {
        setSpacing(8);
        setPadding(new Insets(8));

        Label title = new Label("Parâmetros");
        title.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        switch (operation) {
            case "Limiarização" -> {
                add(grid, 0, "Limiar:", threshold);
                add(grid, 1, "Faixa:", new Label("0 → 255"));
            }
            case "Gamma" -> {
                add(grid, 0, "Gamma (γ):", gamma);
                add(grid, 1, "Constante c:", gammaC);
            }
            case "Logaritmo" -> {
                add(grid, 0, "Constante c:", logC);
                add(grid, 1, "Padrão:", new Label("255 / ln(256) ≈ 45,99"));
            }
            case "Alargamento de contraste" -> {
                add(grid, 0, "r1:", r1);
                add(grid, 1, "s1:", s1);
                add(grid, 2, "r2:", r2);
                add(grid, 3, "s2:", s2);
            }
            case "Contraste adaptativo" -> {
                add(grid, 0, "Constante c:", adaptiveC);
                add(grid, 1, "Vizinhança:", new Label(
                        "Use o campo Vizinhança acima"
                ));
            }
            case "Aguçamento" -> {
                add(grid, 0, "Constante c:", sharpenC);
                add(grid, 1, "Constante d:", sharpenD);
            }
            case "High boost" -> {
                add(grid, 0, "Fator A:", highBoostA);
                add(grid, 1, "A > 1 reforça os detalhes", new Label(""));
            }
            default -> {
                add(grid, 0, "Parâmetros:", new Label(
                        "Esta operação usa os parâmetros padrão."
                ));
            }
        }

        Label help = new Label(description(operation));
        help.setWrapText(true);
        help.getStyleClass().add("parameter-help");

        getChildren().addAll(title, grid, help);
        getStyleClass().add("parameter-card");
    }

    private static void add(GridPane grid, int row, String label, javafx.scene.Node node) {
        grid.add(new Label(label), 0, row);
        grid.add(node, 1, row);
    }

    private static TextField field(String value) {
        TextField field = new TextField(value);
        field.setPrefWidth(110);
        return field;
    }

    private static String description(String operation) {
        return switch (operation) {
            case "Limiarização" ->
                    "Pixels menores que o limiar recebem 0; pixels maiores ou iguais recebem 255.";
            case "Gamma" ->
                    "s = c · r^γ. Gamma menor que 1 tende a clarear regiões escuras.";
            case "Logaritmo" ->
                    "s = c · ln(1 + r). O padrão usa c = 255/ln(256), mantendo a saída em 0–255.";
            case "Alargamento de contraste" ->
                    "Transformação linear definida pelos pontos (r1,s1) e (r2,s2).";
            case "Contraste adaptativo" ->
                    "Calcula média e desvio padrão locais na vizinhança selecionada e ajusta o contraste em torno da média.";
            case "Aguçamento" ->
                    "Usa a máscara [-c,-c,-c; -c,8c+d,-c; -c,-c,-c]. c=d=1 é o teste obrigatório.";
            case "High boost" ->
                    "Usa g = A·f − f_suave, com suavização pela média 3×3. A deve ser maior que 1.";
            default ->
                    "A operação não exige parâmetros adicionais.";
        };
    }

    public int getThreshold() {
        return parseInt(threshold, "limiar");
    }

    public double getGamma() {
        return parseDouble(gamma, "gamma");
    }

    public double getGammaC() {
        return parseDouble(gammaC, "c do gamma");
    }

    public double getLogC() {
        return parseDouble(logC, "c do logaritmo");
    }

    public int getR1() {
        return parseInt(r1, "r1");
    }

    public int getS1() {
        return parseInt(s1, "s1");
    }

    public int getR2() {
        return parseInt(r2, "r2");
    }

    public int getS2() {
        return parseInt(s2, "s2");
    }

    public double getAdaptiveC() {
        return parseDouble(adaptiveC, "c do contraste adaptativo");
    }

    public double getSharpenC() {
        return parseDouble(sharpenC, "c do aguçamento");
    }

    public double getSharpenD() {
        return parseDouble(sharpenD, "d do aguçamento");
    }

    public double getHighBoostA() {
        return parseDouble(highBoostA, "fator A do high boost");
    }

    private static int parseInt(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para " + name + ": " + field.getText()
            );
        }
    }

    private static double parseDouble(TextField field, String name) {
        try {
            return Double.parseDouble(
                    field.getText().trim().replace(',', '.')
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para " + name + ": " + field.getText()
            );
        }
    }
}
