package Classes.Image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Lee y escribe imágenes en formato PIX:
 * marca "PIX" (3 bytes), ancho (int), alto (int) y después 3 bytes (R, G, B) por cada píxel.
 */
public class ImageHandler {

    // Guarda una imagen en un archivo .pix
    public static void write(Image image, String path) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(path)))) {

            out.writeBytes(image.getHeader().getMAGIC());
            out.writeInt(image.getHeader().getWidth());
            out.writeInt(image.getHeader().getHeight());

            for (Pixel pixel : image.getPixels()) {
                out.writeByte(pixel.getR());
                out.writeByte(pixel.getG());
                out.writeByte(pixel.getB());
            }
        }
    }

    // Lee un archivo .pix y arma la imagen. Si el archivo no es válido, lanza IOException.
    public static Image read(String path) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(path)))) {

            String magicEsperado = new Header(0, 0).getMAGIC();
            byte[] magic = new byte[magicEsperado.length()];
            in.readFully(magic);
            if (!new String(magic).equals(magicEsperado)) {
                throw new IOException("Formato inválido: no es un archivo PIX");
            }

            int width = in.readInt();
            int height = in.readInt();
            if (width <= 0 || height <= 0) {
                throw new IOException("Formato inválido: tamaño incorrecto");
            }

            Image image = new Image(new Header(width, height));
            int total = width * height;
            for (int i = 0; i < total; i++) {
                int r = in.readUnsignedByte();
                int g = in.readUnsignedByte();
                int b = in.readUnsignedByte();
                image.addPixel(new Pixel(r, g, b));
            }
            return image;
        }
    }
}
