package Classes.Image;

import java.util.ArrayList;
import java.util.List;

// una imagen de pixeles. los guarda en una lista en el mismo orden en que se dibujan
public class Image {
    private Header header;
    private List<Pixel> pixels;

    //constructores
    public Image(Header header) {
        this.header = header;
        this.pixels = new ArrayList<Pixel>();
    }

    public Image(Header header, List<Pixel> pixels) {
        this.header = header;
        this.pixels = pixels;
    }

    //getters y setters
    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }

    public List<Pixel> getPixels() { return pixels; }
    public void setPixels(List<Pixel> pixels) { this.pixels = pixels; }

    // cambia el pixel que esta en la posicion (x, y)
    public void setPixel(Pixel pixel, int x, int y) {
        pixels.set(y * header.getWidth() + x, pixel);
    }

    // agrega un pixel al final. si la imagen ya esta llena, avisa
    public void addPixel(Pixel pixel) {
        if (pixels.size() >= header.getWidth() * header.getHeight()) {
            throw new IllegalStateException("la imagen ya esta llena");
        }
        pixels.add(pixel);
    }

    // devuelve el pixel que esta en la posicion (x, y)
    public Pixel getPixel(int x, int y) {
        return pixels.get(y * header.getWidth() + x);
    }

    // dice si la imagen ya tiene todos los pixeles que necesita
    public boolean isComplete() {
        return pixels.size() == header.getWidth() * header.getHeight();
    }
}
