package imagelab.controller;

import imagelab.model.GrayImage;
import imagelab.model.Kernel;
import imagelab.model.ProcessingResult;
import imagelab.processing.AdaptiveContrast;
import imagelab.processing.AlgebraicOperations;
import imagelab.processing.Convolution;
import imagelab.processing.EdgeDetection;
import imagelab.processing.HistogramOperations;
import imagelab.processing.IntensityTransformations;
import imagelab.processing.Sharpening;
import imagelab.processing.SpatialFilters;
import imagelab.ui.AnalysisPane;
import imagelab.ui.DissolveParameterPane;
import imagelab.ui.ImageViewPane;
import imagelab.ui.KernelEditorPane;
import imagelab.ui.OperationParameterPane;
import imagelab.util.ImageIO;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Coordena a interface principal e os algoritmos do ImageLab.
 *
 * A regra é simples:
 * - UI cria e edita controles;
 * - Controller lê os parâmetros e decide qual operação executar;
 * - Processing executa os algoritmos;
 * - Model representa imagens e kernels;
 * - Util trata entrada/saída e conversões.
 */
public class MainController {

    private final ImageViewPane originalView;
    private final ImageViewPane resultView;
    private final AnalysisPane analysisPane;
    private final VBox parameterBox;

    private GrayImage currentImage;
    private GrayImage currentResult;
    private final Deque<GrayImage> history = new ArrayDeque<>();

    private GrayImage secondImage;
    private GrayImage alphaMaskImage;

    private KernelEditorPane kernelEditor;
    private DissolveParameterPane dissolvePane;
    private OperationParameterPane operationParameters;
    private int lastNeighborhood = 3;
    private Consumer<String> statusHandler = message -> { };

    public MainController(
            ImageViewPane originalView,
            ImageViewPane resultView,
            AnalysisPane analysisPane,
            VBox parameterBox
    ) {
        this.originalView = originalView;
        this.resultView = resultView;
        this.analysisPane = analysisPane;
        this.parameterBox = parameterBox;
    }

    /**
     * Mantém compatibilidade com a construção anterior do controller.
     */
    public MainController(
            Window ignoredWindow,
            ImageViewPane originalView,
            ImageViewPane resultView,
            AnalysisPane analysisPane
    ) {
        this(
                originalView,
                resultView,
                analysisPane,
                new VBox(8)
        );
    }

    /** Define o destino das mensagens curtas exibidas na barra de status. */
    public void setStatusHandler(Consumer<String> statusHandler) {
        this.statusHandler = statusHandler == null ? message -> { } : statusHandler;
    }

    public void openImage(Window window) {
        File selected = chooseImageFile(window, "Abrir imagem");
        if (selected == null) {
            return;
        }

        try {
            currentImage = ImageIO.readGrayscale(selected);
            currentResult = currentImage.copy();
            history.clear();

            // Uma nova imagem invalida os recursos auxiliares do dissolve.
            secondImage = null;
            alphaMaskImage = null;

            originalView.setImage(ImageIO.toFxImage(currentImage));
            resultView.setImage(ImageIO.toFxImage(currentResult));

            analysisPane.setText(
                    "Imagem carregada.\n\n"
                            + "Arquivo: " + selected.getName() + "\n"
                            + "Dimensão: " + currentImage.getWidth()
                            + " × " + currentImage.getHeight() + " pixels\n"
                            + "Formato interno: escala de cinza 8 bits"
            );
            analysisPane.setHistogram(null);
            analysisPane.setMaskDescription(null);
            analysisPane.setIntermediates(null);

            updateDissolveLabels();
            setStatus("Imagem carregada: " + selected.getName());

        } catch (IOException | RuntimeException ex) {
            showError(
                    "Não foi possível abrir a imagem.",
                    ex.getMessage()
            );
        }
    }

    public void openImage() {
        openImage(getCurrentWindow());
    }

    /**
     * Atualiza somente os controles necessários para a operação escolhida.
     */
    public void updateParameters(String operation, Window window) {
        parameterBox.getChildren().clear();
        kernelEditor = null;
        dissolvePane = null;
        operationParameters = null;

        if (operation == null) {
            return;
        }

        switch (operation) {
            case "Convolução genérica" -> buildConvolutionParameters();
            case "Dissolve uniforme" -> buildDissolveParameters(false);
            case "Dissolve não uniforme" -> buildDissolveParameters(true);
            case "Limiarização",
                 "Alargamento de contraste",
                 "Gamma",
                 "Logaritmo",
                 "Aguçamento",
                 "High boost",
                 "Contraste adaptativo" -> buildOperationParameters(operation);
            default -> {
                Label label = new Label(
                        "Esta operação não exige parâmetros adicionais."
                );
                label.getStyleClass().add("parameter-help");
                parameterBox.getChildren().add(label);
            }
        }
    }

    /**
     * Compatibilidade com chamadas antigas.
     */
    public void updateParameters(String operation, Object ignored) {
        updateParameters(operation, (Window) null);
    }


    private void buildOperationParameters(String operation) {
        operationParameters = new OperationParameterPane(operation);
        parameterBox.getChildren().add(operationParameters);
    }

    private void buildConvolutionParameters() {
        kernelEditor = new KernelEditorPane();
        parameterBox.getChildren().add(kernelEditor);
    }

    private void buildDissolveParameters(boolean nonUniform) {
        dissolvePane = new DissolveParameterPane(nonUniform);

        dissolvePane.setOnSecondImage(
                () -> loadSecondImage(getCurrentWindow())
        );

        if (nonUniform) {
            dissolvePane.setOnMask(
                    () -> loadAlphaMask(getCurrentWindow())
            );
        }

        parameterBox.getChildren().add(dissolvePane);
        updateDissolveLabels();
    }

    private void updateDissolveLabels() {
        if (dissolvePane == null) {
            return;
        }

        dissolvePane.setSecondImageInfo(
                secondImage == null
                        ? "Nenhuma imagem selecionada"
                        : secondImage.getWidth()
                                + " × "
                                + secondImage.getHeight()
                                + " px"
        );

        if (dissolvePane.isNonUniform()) {
            dissolvePane.setMaskInfo(
                    alphaMaskImage == null
                            ? "Nenhuma máscara selecionada"
                            : alphaMaskImage.getWidth()
                                    + " × "
                                    + alphaMaskImage.getHeight()
                                    + " px"
            );
        }
    }

    private void loadSecondImage(Window window) {
        File selected = chooseImageFile(
                window,
                "Selecionar segunda imagem"
        );

        if (selected == null) {
            return;
        }

        try {
            secondImage = ImageIO.readGrayscale(selected);
            updateDissolveLabels();

            analysisPane.setText(
                    "Segunda imagem carregada.\n\n"
                            + "Arquivo: " + selected.getName() + "\n"
                            + "Dimensão: " + secondImage.getWidth()
                            + " × " + secondImage.getHeight() + " pixels\n\n"
                            + "Ela será utilizada como imagem B no dissolve."
            );

        } catch (IOException | RuntimeException ex) {
            showError(
                    "Não foi possível abrir a segunda imagem.",
                    ex.getMessage()
            );
        }
    }

    private void loadAlphaMask(Window window) {
        File selected = chooseImageFile(
                window,
                "Selecionar máscara de alpha"
        );

        if (selected == null) {
            return;
        }

        try {
            alphaMaskImage = ImageIO.readGrayscale(selected);
            updateDissolveLabels();

            analysisPane.setText(
                    "Máscara de alpha carregada.\n\n"
                            + "Arquivo: " + selected.getName() + "\n"
                            + "Dimensão: " + alphaMaskImage.getWidth()
                            + " × " + alphaMaskImage.getHeight() + " pixels\n\n"
                            + "0 favorece a imagem original.\n"
                            + "255 favorece a 2ª imagem."
            );
            analysisPane.setIntermediates(Map.of("Máscara α", alphaMaskImage));

        } catch (IOException | RuntimeException ex) {
            showError(
                    "Não foi possível abrir a máscara.",
                    ex.getMessage()
            );
        }
    }

    private File chooseImageFile(Window window, String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens",
                        "*.png", "*.jpg", "*.jpeg",
                        "*.bmp", "*.tif", "*.tiff"
                )
        );
        return chooser.showOpenDialog(window);
    }

    /**
     * Executa a operação selecionada.
     */
    public void applyOperation(String operation, int neighborhood) {
        if (currentImage == null) {
            showError(
                    "Nenhuma imagem carregada.",
                    "Abra uma imagem antes de aplicar uma operação."
            );
            return;
        }

        if (operation == null || operation.isBlank()) {
            showError(
                    "Nenhuma operação selecionada.",
                    "Selecione uma operação antes de clicar em Aplicar."
            );
            return;
        }

        try {
            boolean usesNeighborhood = usesNeighborhood(operation);
            if (usesNeighborhood) {
                if (neighborhood < 1 || neighborhood > 15 || neighborhood % 2 == 0) {
                    throw new IllegalArgumentException(
                            "A vizinhança deve ser ímpar entre 1 e 15."
                    );
                }
                lastNeighborhood = neighborhood;
            }
            GrayImage source = currentResult == null ? currentImage : currentResult;
            GrayImage beforeOperation = source.copy();
            ProcessingResult processingResult = null;

            switch (operation) {
                case "Negativo" ->
                        currentResult =
                                IntensityTransformations.negative(source);

                case "Limiarização" ->
                        currentResult =
                                IntensityTransformations.threshold(
                                        source, requireParameters().getThreshold()
                                );

                case "Alargamento de contraste" ->
                        currentResult =
                                IntensityTransformations.contrastStretch(
                                        source,
                                        requireParameters().getR1(),
                                        requireParameters().getS1(),
                                        requireParameters().getR2(),
                                        requireParameters().getS2()
                                );

                case "Expansão de histograma" ->
                        currentResult =
                                HistogramOperations.expansion(source);

                case "Gamma" ->
                        currentResult =
                                IntensityTransformations.gamma(
                                        source,
                                        requireParameters().getGamma(),
                                        requireParameters().getGammaC()
                                );

                case "Logaritmo" ->
                        currentResult =
                                IntensityTransformations.logarithm(
                                        source,
                                        requireParameters().getLogC()
                                );

                case "Histograma" ->
                        currentResult = source.copy();

                case "Equalização" ->
                        currentResult =
                                HistogramOperations.equalization(source);

                case "Filtro da média" ->
                        currentResult =
                                SpatialFilters.mean(
                                        source, neighborhood
                                );

                case "Filtro da mediana" ->
                        currentResult =
                                SpatialFilters.median(
                                        source, neighborhood
                                );

                case "Roberts" -> {
                    processingResult = EdgeDetection.roberts(source);
                    currentResult = processingResult.getImage();
                }

                case "Sobel" -> {
                    processingResult = EdgeDetection.sobel(source);
                    currentResult = processingResult.getImage();
                }

                case "Kirsch" -> {
                    processingResult = EdgeDetection.kirsch(source);
                    currentResult = processingResult.getImage();
                }

                case "Laplaciano" -> {
                    processingResult =
                            EdgeDetection.laplacian(source);
                    currentResult = processingResult.getImage();
                }

                case "Aguçamento" -> {
                    double c = requireParameters().getSharpenC();
                    double d = requireParameters().getSharpenD();
                    processingResult = Sharpening.laplacian(source, c, d);
                    currentResult = processingResult.getImage();
                }

                case "High boost" -> {
                    double a = requireParameters().getHighBoostA();
                    processingResult = Sharpening.highBoostResult(source, a);
                    currentResult = processingResult.getImage();
                }

                case "Convolução genérica" ->
                        currentResult = applyGenericConvolution();

            case "Contraste adaptativo" ->
                        currentResult =
                                AdaptiveContrast.apply(
                                        source,
                                        requireParameters().getAdaptiveC(),
                                        neighborhood
                                );

                case "Dissolve uniforme" ->
                        currentResult = applyUniformDissolve();

                case "Dissolve não uniforme" ->
                        currentResult = applyNonUniformDissolve();

                default -> {
                    showError(
                            "Operação desconhecida.",
                            operation
                    );
                    return;
                }
            }

            history.push(beforeOperation);
            resultView.setImage(ImageIO.toFxImage(currentResult));
            updateAnalysis(operation, processingResult);
            setStatus("Concluído: " + operation);

        } catch (RuntimeException ex) {
            showError(
                    "Erro ao executar a operação.",
                    ex.getMessage()
            );
        }
    }

    private boolean usesNeighborhood(String operation) {
        return operation != null && (
                operation.equals("Filtro da média")
                        || operation.equals("Filtro da mediana")
                        || operation.equals("Contraste adaptativo")
        );
    }

    private OperationParameterPane requireParameters() {
        if (operationParameters == null) {
            throw new IllegalArgumentException(
                    "Os parâmetros da operação não foram inicializados."
            );
        }
        return operationParameters;
    }

    private GrayImage applyGenericConvolution() {
        if (kernelEditor == null) {
            throw new IllegalArgumentException(
                    "O editor da máscara não foi inicializado."
            );
        }

        Kernel kernel = kernelEditor.getKernel();
        return Convolution.apply(currentResult == null ? currentImage : currentResult, kernel);
    }

    private GrayImage applyUniformDissolve() {
        validateSecondImage();

        if (dissolvePane == null || dissolvePane.isNonUniform()) {
            throw new IllegalArgumentException(
                    "Os parâmetros do dissolve uniforme não foram inicializados."
            );
        }

        double alpha = dissolvePane.getAlpha();

        if (alpha < 0.0 || alpha > 1.0) {
            throw new IllegalArgumentException(
                    "Alpha deve estar entre 0 e 1."
            );
        }

        return AlgebraicOperations.dissolveUniform(
                currentResult == null ? currentImage : currentResult,
                secondImage,
                alpha
        );
    }

    private GrayImage applyNonUniformDissolve() {
        validateSecondImage();

        if (alphaMaskImage == null) {
            throw new IllegalArgumentException(
                    "Selecione uma máscara de alpha antes de aplicar "
                            + "o dissolve não uniforme."
            );
        }

        return AlgebraicOperations.dissolveNonUniform(
                currentResult == null ? currentImage : currentResult,
                secondImage,
                alphaMaskImage
        );
    }

    private void validateSecondImage() {
        if (secondImage == null) {
            throw new IllegalArgumentException(
                    "Selecione uma segunda imagem antes de aplicar o dissolve."
            );
        }

        GrayImage source = currentResult == null ? currentImage : currentResult;
        if (source.getWidth() != secondImage.getWidth()
                || source.getHeight() != secondImage.getHeight()) {
            throw new IllegalArgumentException(
                    "A imagem de entrada e a segunda imagem precisam ter "
                            + "o mesmo tamanho."
            );
        }
    }

    /** Restaura o resultado para uma cópia da imagem original. */
    public void resetResult() {
        if (currentImage == null) {
            setStatus("Nenhuma imagem carregada.");
            return;
        }
        currentResult = currentImage.copy();
        history.clear();
        resultView.setImage(ImageIO.toFxImage(currentResult));
        analysisPane.setText("Resultado restaurado para a imagem original.\n\nSelecione uma operação e clique em Aplicar.");
        analysisPane.setHistogram(null);
        analysisPane.setMaskDescription(null);
        analysisPane.setIntermediates(null);
        setStatus("Resultado restaurado.");
    }

    /** Desfaz apenas a última operação aplicada ao resultado atual. */
    public void undoLastOperation() {
        if (currentImage == null) {
            setStatus("Nenhuma imagem carregada.");
            return;
        }
        if (history.isEmpty()) {
            setStatus("Não há operação para desfazer.");
            return;
        }

        currentResult = history.pop();
        resultView.setImage(ImageIO.toFxImage(currentResult));
        analysisPane.setText(
                "Última operação desfeita.\n\n"
                        + "O resultado voltou ao estado anterior."
        );
        analysisPane.setHistogram(null);
        analysisPane.setMaskDescription(null);
        analysisPane.setIntermediates(null);
        setStatus("Última operação desfeita.");
    }

    public void saveResult(Window window) {
        if (currentResult == null) {
            showError(
                    "Nenhum resultado disponível.",
                    "Aplique uma operação antes de salvar."
            );
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar resultado");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        File selected = chooser.showSaveDialog(window);
        if (selected == null) {
            return;
        }

        try {
            ImageIO.writeGrayscale(currentResult, selected);
            showInfo(
                    "Resultado salvo",
                    "Arquivo salvo em:\n" + selected.getAbsolutePath()
            );
            setStatus("Resultado salvo: " + selected.getName());
        } catch (IOException | RuntimeException ex) {
            showError(
                    "Não foi possível salvar o resultado.",
                    ex.getMessage()
            );
        }
    }

    public void saveResult() {
        saveResult(getCurrentWindow());
    }

    /**
     * Monta o texto do modo de análise sem executar processamento.
     */
    private void updateAnalysis(
            String operation,
            ProcessingResult processingResult
    ) {
        StringBuilder text = new StringBuilder();

        text.append("Operação: ")
                .append(operation)
                .append("\n\n");

        text.append("Imagem: ")
                .append(currentImage.getWidth())
                .append(" × ")
                .append(currentImage.getHeight())
                .append(" pixels\n\n");

        if (processingResult != null) {
            Map<String, GrayImage> intermediates =
                    processingResult.getIntermediates();

            analysisPane.setIntermediates(intermediates);

            if (!intermediates.isEmpty()) {
                text.append("Respostas intermediárias disponíveis: ")
                        .append(intermediates.size())
                        .append("\n\n");
            }
        } else {
            analysisPane.setIntermediates(null);
        }

        analysisPane.setMaskDescription(maskDescription(operation));

        if (operation.equals("Histograma")
                || operation.contains("histograma")
                || operation.equals("Equalização")
                || operation.equals("Alargamento de contraste")) {
            appendHistogram(text, operation.equals("Histograma"));
        } else {
            analysisPane.setHistogram(null);
        }

        if (operation.equals("Convolução genérica")
                && kernelEditor != null) {
            Kernel kernel = kernelEditor.getKernel();

            analysisPane.setMaskDescription(
                    kernelEditor.getDescription()
                            + "\nSoma dos coeficientes: " + format(kernel.getSum())
                            + "\nOffset: " + kernel.getOffset()
                            + "\nBordas: replicação do pixel mais próximo"
                            + "\nSaída: saturação em [0,255]."
            );

            text.append("\nMáscara aplicada:\n")
                    .append(kernelEditor.getDescription())
                    .append("\n")
                    .append("Soma dos coeficientes: ")
                    .append(format(kernel.getSum()))
                    .append("\n")
                    .append("Bordas: replicação do pixel mais próximo")
                    .append("\n")
                    .append("Valores finais: limitados ao intervalo [0,255].");
        }

        if (operationParameters != null) {
            appendParameterAnalysis(text, operation);
        }

        if (operation.equals("Dissolve uniforme")
                || operation.equals("Dissolve não uniforme")) {
            appendDissolveAnalysis(text, operation);
        }

        analysisPane.setText(text.toString());
    }

    private String maskDescription(String operation) {
        return switch (operation) {
            case "Roberts" ->
                    "Roberts — Gx = [1 0; 0 -1]\n"
                            + "Gy = [0 1; -1 0]\n"
                            + "Resposta final: magnitude √(Gx² + Gy²).";
            case "Sobel" ->
                    "Sobel\n"
                            + "Gx = [-1 0 1; -2 0 2; -1 0 1]\n"
                            + "Gy = [-1 -2 -1; 0 0 0; 1 2 1]\n"
                            + "Resposta final: magnitude √(Gx² + Gy²).";
            case "Kirsch" ->
                    "Kirsch — 8 máscaras direcionais (N, NE, E, SE, S, SO, O, NO).\n"
                            + "A resposta final seleciona a maior resposta entre as direções.";
            case "Laplaciano" ->
                    "Laplaciano — [0 1 0; 1 -4 1; 0 1 0].\n"
                            + "A resposta visual é o módulo da resposta assinada.";
            case "Aguçamento" ->
                    "Aguçamento — [-c -c -c; -c 8c+d -c; -c -c -c].\n"
                            + "A soma dos coeficientes é d; a resposta do Laplaciano é exibida separadamente.";
            case "High boost" ->
                    "High boost — g = A·f − f_suave.\n"
                            + "A > 1 e suavização por média 3×3.";
            default -> null;
        };
    }

    private void appendParameterAnalysis(StringBuilder text, String operation) {
        text.append("Parâmetros utilizados:\n");

        switch (operation) {
            case "Limiarização" ->
                    text.append("• Limiar: ")
                            .append(requireParameters().getThreshold())
                            .append("\n");
            case "Alargamento de contraste" ->
                    text.append("• (r1,s1)=(")
                            .append(requireParameters().getR1()).append(",")
                            .append(requireParameters().getS1()).append("), ")
                            .append("(r2,s2)=(")
                            .append(requireParameters().getR2()).append(",")
                            .append(requireParameters().getS2()).append(")\n");
            case "Gamma" ->
                    text.append("• γ: ")
                            .append(format(requireParameters().getGamma()))
                            .append("\n• c: ")
                            .append(format(requireParameters().getGammaC()))
                            .append("\n");
            case "Logaritmo" ->
                    text.append("• c: ")
                            .append(format(requireParameters().getLogC()))
                            .append("\n");
            case "Aguçamento" ->
                    text.append("• c: ")
                            .append(format(requireParameters().getSharpenC()))
                            .append("\n• d: ")
                            .append(format(requireParameters().getSharpenD()))
                            .append("\n• Máscara: [-c -c -c; -c 8c+d -c; -c -c -c]\n")
                            .append("• Resposta do Laplaciano exibida separadamente\n");
            case "Contraste adaptativo" ->
                    text.append("• c: ")
                            .append(format(requireParameters().getAdaptiveC()))
                            .append("\n• Vizinhança: ")
                            .append(neighborhoodSizeText())
                            .append("\n• Fórmula: s = média + c·(r − média)/desvio padrão\n");
            case "High boost" ->
                    text.append("• A: ")
                            .append(format(requireParameters().getHighBoostA()))
                            .append("\n• Fórmula: g = A·f − f_suave\n• Suavização: média 3×3\n");
            default -> {
                return;
            }
        }

        text.append("\n");
    }

    private String neighborhoodSizeText() {
        // O mesmo tamanho de vizinhança é usado pela operação aplicada e pela análise.
        return Integer.toString(lastNeighborhood) + " × " + lastNeighborhood;
    }

    private void appendHistogram(StringBuilder text, boolean originalImage) {
        int[] histogram =
                HistogramOperations.histogram(currentResult);

        int min = 0;
        while (min < 256 && histogram[min] == 0) {
            min++;
        }

        int max = 255;
        while (max >= 0 && histogram[max] == 0) {
            max--;
        }

        int nonZeroLevels = 0;
        long total = 0;
        long weighted = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > 0) {
                nonZeroLevels++;
            }
            total += histogram[i];
            weighted += (long) i * histogram[i];
        }
        double mean = total == 0 ? 0 : (double) weighted / total;

        text.append(originalImage ? "Histograma da imagem de entrada:\n" : "Histograma do resultado:\n")
                .append("• Níveis presentes: ")
                .append(nonZeroLevels)
                .append("\n")
                .append("• Menor intensidade: ")
                .append(min)
                .append("\n")
                .append("• Maior intensidade: ")
                .append(max)
                .append("\n")
                .append("• Intensidade média: ")
                .append(format(mean))
                .append("\n")
                .append("• Pixels: ")
                .append(total)
                .append("\n");

        analysisPane.setHistogram(histogram);
    }

    private void appendDissolveAnalysis(
            StringBuilder text,
            String operation
    ) {
        text.append("Segunda imagem: ");

        if (secondImage == null) {
            text.append("não selecionada\n");
        } else {
            text.append(secondImage.getWidth())
                    .append(" × ")
                    .append(secondImage.getHeight())
                    .append(" pixels\n");
        }

        if (operation.equals("Dissolve uniforme")) {
            double alpha =
                    dissolvePane == null
                            ? 0.5
                            : dissolvePane.getAlpha();

            text.append("Alpha: ")
                    .append(format(alpha))
                    .append("\n")
                    .append("Fórmula: g = (1 − α)A + αB");
        } else {
            text.append("Máscara de alpha: ");

            if (alphaMaskImage == null) {
                text.append("não selecionada\n");
            } else {
                text.append(alphaMaskImage.getWidth())
                        .append(" × ")
                        .append(alphaMaskImage.getHeight())
                        .append(" pixels\n");
            }

            text.append(
                    "Fórmula: g(x,y) = (1 − α(x,y))A(x,y) "
                            + "+ α(x,y)B(x,y)\n"
                            + "α(x,y) = intensidade da máscara / 255"
            );
        }
    }

    private static String format(double value) {
        return value == Math.rint(value)
                ? Long.toString((long) value)
                : Double.toString(value);
    }

    private Window getCurrentWindow() {
        return originalView.getScene() == null
                ? null
                : originalView.getScene().getWindow();
    }

    public GrayImage getCurrentImage() {
        return currentImage;
    }

    public GrayImage getCurrentResult() {
        return currentResult;
    }

    private void setStatus(String message) {
        statusHandler.accept(message);
    }

    private void showError(String header, String message) {
        setStatus(header);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ImageLab");
        alert.setHeaderText(header);
        alert.setContentText(
                message == null ? "Erro desconhecido." : message
        );
        alert.showAndWait();
    }

    private void showInfo(String header, String message) {
        setStatus(header);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ImageLab");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
