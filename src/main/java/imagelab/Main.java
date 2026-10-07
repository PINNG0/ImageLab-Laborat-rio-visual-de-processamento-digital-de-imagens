package imagelab;

import imagelab.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Ponto de entrada da aplicação JavaFX.
 *
 * A janela inicia maximizada, mas continua sendo uma janela normal do
 * sistema operacional. F11 alterna entre maximizada e restaurada e Esc
 * restaura a janela caso ela esteja maximizada ou em tela cheia.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainView view = new MainView(stage);

        Scene scene = new Scene(view, 1500, 850);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        stage.setTitle("ImageLab");
        stage.setMinWidth(1180);
        stage.setMinHeight(700);
        stage.setResizable(true);

        // A aplicação nunca inicia no modo de tela cheia exclusivo.
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");

        stage.setScene(scene);
        stage.show();

        /*
         * Maximiza depois de mostrar a janela para manter a barra
         * de título e os controles normais do Windows.
         */
        stage.setMaximized(true);

        scene.addEventHandler(
                KeyEvent.KEY_PRESSED,
                event -> handleWindowShortcut(stage, event)
        );
    }

    /**
     * Mantém o comportamento esperado de F11/Esc sem usar tela cheia
     * exclusiva do JavaFX.
     */
    private void handleWindowShortcut(
            Stage stage,
            KeyEvent event
    ) {
        if (event.getCode() == KeyCode.F11) {
            stage.setFullScreen(false);
            stage.setMaximized(!stage.isMaximized());
            event.consume();
            return;
        }

        if (event.getCode() == KeyCode.ESCAPE) {
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);
                stage.setMaximized(true);
                event.consume();
            } else if (stage.isMaximized()) {
                stage.setMaximized(false);
                event.consume();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}