package Classes.Image;

import java.util.ArrayList;
import java.util.List;

public class Image {
    private Header header;
    private List<Pixel> pixels;

    public Image(Header header) {
        this.header = header;
        this.pixels = new ArrayList<>();
    }

    public Image(Header header, List<Pixel> pixels) {
        this.header = header;
        this.pixels = new ArrayList<>();
    }

    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }

    public List<Pixel> getPixels() { return pixels; }
    public void setPixels(List<Pixel> pixels) { this.pixels = pixels; }

    public void setPixel(Pixel pixel, int x, int y) {
        pixels.set(y * header.getWidth() + x, pixel);
    }

    public void addPixel(Pixel pixel) {
        if (pixels.size() >= header.getWidth() * header.getHeight()) {
            throw new IllegalStateException("Image is already full");
        }
        pixels.add(pixel);
    }

    public Pixel getPixel(int x, int y) {
        return pixels.get(y * header.getWidth() + x);
    }

    public boolean isComplete() {
        return pixels.size() == header.getWidth() * header.getHeight();
    }
}
