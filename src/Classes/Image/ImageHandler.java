package Classes.Image;
import java.io.*;

public class ImageHandler {
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
}
