package Classes;

import java.io.DataInputStream;
import java.io.IOException;

public class Header {
    private String MAGIC = "PIX";
    private int width;
    private int height;

    public Header(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getMAGIC() { return MAGIC; }
    public void setMAGIC(String MAGIC) { this.MAGIC = MAGIC; }

    public Header read(DataInputStream in) throws IOException {
        byte[] magic = new byte[4];
        in.readFully(magic);

        if (!new String(magic).equals(MAGIC)) {
            System.out.println("Invalid format");
        }

        int width = in.readInt();
        int height = in.readInt();

        if(width <= 0 || height <= 0) {
            System.out.println("Invalid format");
        }

        return new Header(width, height);
    }
}
