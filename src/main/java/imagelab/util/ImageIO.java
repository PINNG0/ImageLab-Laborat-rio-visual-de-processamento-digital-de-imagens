package imagelab.util;

import imagelab.model.GrayImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Responsável pela leitura, escrita e conversão de imagens.
 *
 * As operações de processamento não são realizadas por esta classe.
 * Ela cuida apenas da entrada e saída dos arquivos e da conversão
 * entre GrayImage e imagens utilizadas pela interface JavaFX.
 */
public final class ImageIO {

    private ImageIO() {
    }

    /**
     * Lê uma imagem de arquivo e converte seu conteúdo para
     * escala de cinza utilizando a combinação ponderada dos
     * canais vermelho, verde e azul.
     */
    public static GrayImage readGrayscale(File file)
            throws IOException {

        BufferedImage buffered =
                javax.imageio.ImageIO.read(file);

        if (buffered == null) {
            throw new IOException(
                    "O arquivo não contém uma imagem reconhecida."
            );
        }

        int width = buffered.getWidth();
        int height = buffered.getHeight();

        GrayImage image =
                new GrayImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb =
                        buffered.getRGB(x, y);

                int red =
                        (rgb >> 16) & 0xFF;

                int green =
                        (rgb >> 8) & 0xFF;

                int blue =
                        rgb & 0xFF;

                int gray =
                        (int) Math.round(
                                0.299 * red
                                        + 0.587 * green
                                        + 0.114 * blue
                        );

                image.set(x, y, gray);
            }
        }

        return image;
    }

    /**
     * Salva uma GrayImage em um arquivo de imagem.
     *
     * O formato padrão é PNG. Arquivos com extensão JPG ou JPEG
     * são gravados nesse formato quando solicitado pelo usuário.
     */
    public static void writeGrayscale(
            GrayImage image,
            File file
    ) throws IOException {

        BufferedImage buffered =
                new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY
                );

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int value =
                        PixelUtils.clamp(
                                image.get(x, y)
                        );

                int rgb =
                        (value << 16)
                                | (value << 8)
                                | value;

                buffered.setRGB(x, y, rgb);
            }
        }

        String format = "png";

        String name =
                file.getName().toLowerCase();

        if (name.endsWith(".jpg")
                || name.endsWith(".jpeg")) {
            format = "jpg";
        }

        javax.imageio.ImageIO.write(
                buffered,
                format,
                file
        );
    }

    /**
     * Converte uma GrayImage para uma imagem exibível pelo JavaFX.
     */
    public static Image toFxImage(
            GrayImage image
    ) {
        int width = image.getWidth();
        int height = image.getHeight();

        WritableImage fxImage =
                new WritableImage(
                        width,
                        height
                );

        PixelWriter writer =
                fxImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int value =
                        PixelUtils.clamp(
                                image.get(x, y)
                        );

                int argb =
                        0xFF000000
                                | (value << 16)
                                | (value << 8)
                                | value;

                writer.setArgb(
                        x,
                        y,
                        argb
                );
            }
        }

        return fxImage;
    }
}