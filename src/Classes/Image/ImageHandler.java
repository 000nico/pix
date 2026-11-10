package Classes.Image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

// lee y escribe imagenes en formato pix: la marca, el ancho, el alto y despues 3 bytes (r, g, b) por cada pixel
public class ImageHandler {

    // guarda una imagen en un archivo .pix
    public static void write(Image image, String path) throws IOException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
        try {
            out.writeBytes(image.getHeader().getMagic());
            out.writeInt(image.getHeader().getWidth());
            out.writeInt(image.getHeader().getHeight());

            List<Pixel> pixels = image.getPixels();
            for (int i = 0; i < pixels.size(); i++) {
                Pixel pixel = pixels.get(i);
                out.writeByte(pixel.getR());
                out.writeByte(pixel.getG());
                out.writeByte(pixel.getB());
            }
        } finally {
            out.close();
        }
    }

    // lee un archivo .pix y arma la imagen. si el archivo no es valido lanza ioexception
    public static Image read(String path) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        try {
            // el header ya sabe leer su parte del archivo
            Header header = Header.read(in);
            Image image = new Image(header);

            int total = header.getWidth() * header.getHeight();
            for (int i = 0; i < total; i++) {
                int r = in.readUnsignedByte();
                int g = in.readUnsignedByte();
                int b = in.readUnsignedByte();
                image.addPixel(new Pixel(r, g, b));
            }
            return image;
        } finally {
            in.close();
        }
    }
}
