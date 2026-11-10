package Classes.Image;

import java.io.DataInputStream;
import java.io.IOException;

// encabezado del archivo pix: la marca "pix", el ancho y el alto de la imagen
public class Header {
    private String magic = "PIX";
    private int width;
    private int height;

    //constructores
    public Header() { }

    public Header(int width, int height) {
        this.width = width;
        this.height = height;
    }

    //getters y setters
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getMagic() { return magic; }
    public void setMagic(String magic) { this.magic = magic; }

    // lee la marca, el ancho y el alto desde el archivo. lo usa el imagehandler
    public static Header read(DataInputStream in) throws IOException {
        byte[] magicLeido = new byte[3];
        in.readFully(magicLeido);

        if (!new String(magicLeido).equals("PIX")) {
            throw new IOException("formato invalido: no es un archivo pix");
        }

        int width = in.readInt();
        int height = in.readInt();

        if (width <= 0 || height <= 0) {
            throw new IOException("formato invalido: el tamanio no puede ser 0");
        }

        return new Header(width, height);
    }
}
