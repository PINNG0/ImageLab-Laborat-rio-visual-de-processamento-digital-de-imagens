package imagelab.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

/**
 * Visualizador de imagem com moldura, zoom e navegação interna.
 * O zoom aumenta somente o conteúdo da viewport; o painel externo permanece estável.
 */
public class ImageViewPane extends BorderPane {

    private final ImageView imageView = new ImageView();
    private final Label title;
    private final Label info = new Label("Nenhuma imagem carregada");
    private final Label empty = new Label("Nenhuma imagem");
    private final Label zoomLabel = new Label("100%");

    private final StackPane imageFrame = new StackPane();
    private final StackPane imageContent = new StackPane();
    private final ScrollPane viewport = new ScrollPane(imageContent);

    private double zoom = 1.0;
    private double baseWidth = 1.0;
    private double baseHeight = 1.0;

    private double dragX;
    private double dragY;
    private double startH;
    private double startV;
    private boolean dragging;
    private BiConsumer<Integer, Integer> pixelHandler;

    public ImageViewPane(String titleText) {
        title = new Label(titleText);

        title.getStyleClass().add("panel-title");
        info.getStyleClass().add("panel-info");
        zoomLabel.getStyleClass().add("zoom-label");
        empty.getStyleClass().add("empty-image");

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setVisible(false);
        imageView.setManaged(false);

        imageFrame.setAlignment(Pos.CENTER);
        imageFrame.getStyleClass().add("image-frame");
        imageFrame.setVisible(false);
        imageFrame.setManaged(false);
        imageFrame.getChildren().add(imageView);

        imageContent.setAlignment(Pos.CENTER);
        imageContent.getStyleClass().add("image-content");
        imageContent.getChildren().addAll(empty, imageFrame);

        viewport.setFitToWidth(false);
        viewport.setFitToHeight(false);
        viewport.setPannable(false);
        viewport.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        viewport.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        viewport.setMinSize(0, 0);
        viewport.setPadding(new Insets(8));
        viewport.getStyleClass().add("image-viewport");

        viewport.viewportBoundsProperty().addListener(
                (obs, oldValue, newValue) -> updateImageSize()
        );

        Button zoomOut = createZoomButton("−");
        Button fit = createZoomButton("Ajustar");
        Button zoomIn = createZoomButton("+");

        zoomOut.setOnAction(e -> setZoom(zoom - 0.25));
        fit.setOnAction(e -> setZoom(1.0));
        zoomIn.setOnAction(e -> setZoom(zoom + 0.25));

        HBox controls = new HBox(
                5,
                zoomOut,
                fit,
                zoomIn,
                zoomLabel
        );

        controls.setAlignment(Pos.CENTER_RIGHT);

        HBox titleLine = new HBox(
                8,
                title,
                controls
        );

        titleLine.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        VBox header = new VBox(
                3,
                titleLine,
                info
        );

        header.getStyleClass().add("panel-header");

        setTop(header);
        setCenter(viewport);
        setMinSize(0, 0);
        setPrefHeight(500);
        getStyleClass().add("image-panel");

        configureNavigation();
        updateImageSize();
    }

    /** Define a imagem e reinicia o visualizador no modo ajustado. */
    public void setImage(Image image) {
        imageView.setImage(image);

        boolean hasImage = image != null;

        imageView.setVisible(hasImage);
        imageView.setManaged(hasImage);
        imageFrame.setVisible(hasImage);
        imageFrame.setManaged(hasImage);
        empty.setVisible(!hasImage);
        empty.setManaged(!hasImage);

        if (!hasImage) {
            info.setText("Nenhuma imagem carregada");
            baseWidth = 1;
            baseHeight = 1;
            setZoom(1.0);
            return;
        }

        info.setText(
                (int) image.getWidth() + " × " + (int) image.getHeight()
                        + " px   ·   arraste para navegar   ·   Ctrl + roda para zoom"
        );

        setZoom(1.0);
        viewport.setHvalue(0.5);
        viewport.setVvalue(0.5);
    }

    /** Configura arraste para navegação e Ctrl+roda para zoom. */
    private void configureNavigation() {
        imageContent.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || zoom <= 1.0) {
                return;
            }

            dragX = event.getSceneX();
            dragY = event.getSceneY();
            startH = viewport.getHvalue();
            startV = viewport.getVvalue();
            dragging = true;
            imageContent.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        });

        imageContent.setOnMouseDragged(event -> {
            if (!dragging || zoom <= 1.0) {
                return;
            }

            double dx = event.getSceneX() - dragX;
            double dy = event.getSceneY() - dragY;

            double extraWidth = Math.max(
                    1,
                    imageContent.getPrefWidth()
                            - viewport.getViewportBounds().getWidth()
            );

            double extraHeight = Math.max(
                    1,
                    imageContent.getPrefHeight()
                            - viewport.getViewportBounds().getHeight()
            );

            viewport.setHvalue(
                    clamp(
                            startH - dx / extraWidth,
                            0,
                            1
                    )
            );

            viewport.setVvalue(
                    clamp(
                            startV - dy / extraHeight,
                            0,
                            1
                    )
            );

            event.consume();
        });

        imageView.setOnMouseMoved(this::handlePixelMove);

        imageContent.setOnMouseReleased(event -> {
            dragging = false;
            imageContent.setCursor(Cursor.DEFAULT);
        });

        viewport.addEventFilter(
                ScrollEvent.SCROLL,
                event -> {
                    if (imageView.getImage() == null) {
                        return;
                    }

                    if (event.isControlDown()) {
                        double oldH = viewport.getHvalue();
                        double oldV = viewport.getVvalue();

                        double step =
                                event.getDeltaY() > 0
                                        ? 0.25
                                        : -0.25;

                        setZoom(zoom + step);

                        viewport.setHvalue(oldH);
                        viewport.setVvalue(oldV);

                        event.consume();

                    } else if (zoom > 1.0) {
                        viewport.setHvalue(
                                clamp(
                                        viewport.getHvalue()
                                                - event.getDeltaX() / 700.0,
                                        0,
                                        1
                                )
                        );

                        viewport.setVvalue(
                                clamp(
                                        viewport.getVvalue()
                                                - event.getDeltaY() / 700.0,
                                        0,
                                        1
                                )
                        );

                        event.consume();
                    }
                }
        );
    }

    private void handlePixelMove(MouseEvent event) {
        if (pixelHandler == null || imageView.getImage() == null) {
            return;
        }

        double width =
                imageView.getBoundsInLocal().getWidth();

        double height =
                imageView.getBoundsInLocal().getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        int x =
                (int) Math.floor(
                        event.getX()
                                / width
                                * imageView.getImage().getWidth()
                );

        int y =
                (int) Math.floor(
                        event.getY()
                                / height
                                * imageView.getImage().getHeight()
                );

        x = (int) clamp(
                x,
                0,
                imageView.getImage().getWidth() - 1
        );

        y = (int) clamp(
                y,
                0,
                imageView.getImage().getHeight() - 1
        );

        pixelHandler.accept(x, y);
    }

    /**
     * Define o callback de inspeção de pixel.
     */
    public void setPixelHandler(
            BiConsumer<Integer, Integer> handler
    ) {
        this.pixelHandler = handler;
    }

    private Button createZoomButton(String text) {
        Button button = new Button(text);

        button.getStyleClass().add(
                "image-tool-button"
        );

        button.setFocusTraversable(false);

        return button;
    }

    /**
     * Mantém a imagem dentro da viewport.
     * O painel externo nunca cresce com o zoom.
     */
    private void updateImageSize() {
        double viewportWidth =
                Math.max(
                        180,
                        viewport.getViewportBounds().getWidth() - 16
                );

        double viewportHeight =
                Math.max(
                        150,
                        viewport.getViewportBounds().getHeight() - 16
                );

        Image image = imageView.getImage();

        if (image != null) {
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();

            double ratio =
                    imageWidth / imageHeight;

            baseWidth = viewportWidth;
            baseHeight = baseWidth / ratio;

            if (baseHeight > viewportHeight) {
                baseHeight = viewportHeight;
                baseWidth = baseHeight * ratio;
            }
        }

        double displayWidth =
                baseWidth * zoom;

        double displayHeight =
                baseHeight * zoom;

        imageView.setFitWidth(displayWidth);
        imageView.setFitHeight(displayHeight);

        imageFrame.setPrefSize(
                displayWidth,
                displayHeight
        );

        imageFrame.setMinSize(
                displayWidth,
                displayHeight
        );

        imageFrame.setMaxSize(
                displayWidth,
                displayHeight
        );

        double contentWidth =
                Math.max(
                        viewportWidth,
                        displayWidth + 16
                );

        double contentHeight =
                Math.max(
                        viewportHeight,
                        displayHeight + 16
                );

        imageContent.setPrefSize(
                contentWidth,
                contentHeight
        );

        imageContent.setMinSize(
                contentWidth,
                contentHeight
        );
    }

    /**
     * Define o zoom relativo ao tamanho ajustado da imagem.
     */
    public void setZoom(double value) {
        zoom = clamp(
                value,
                0.5,
                4.0
        );

        zoomLabel.setText(
                Math.round(zoom * 100) + "%"
        );

        updateImageSize();
    }

    private static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    public ImageView getImageView() {
        return imageView;
    }
}