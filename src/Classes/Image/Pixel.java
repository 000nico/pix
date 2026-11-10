package Classes.Image;

// un pixel de la imagen, guarda los 3 colores basicos
public class Pixel {
    private int r, g, b;

    //constructores
    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    //getters y setters
    public int getR() { return r; }
    public void setR(int r) { this.r = r; }

    public int getG() { return g; }
    public void setG(int g) { this.g = g; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }
}
