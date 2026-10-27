import Classes.Image.Header;
import Classes.Image.Image;
import Classes.Image.ImageVisualizer;
import Classes.Image.Pixel;
public class Main {
    public static void main(String[] args) {
        int width = 100;
        int height = 100;
        Header header = new Header(width, height);
        Image image = new Image(header);

        // Armamos un degradado: rojo crece hacia la derecha, verde hacia abajo
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Distancias a cada centro (teorema de Pitágoras)
                double distCara   = Math.sqrt(Math.pow(x - 50, 2) + Math.pow(y - 50, 2));
                double distOjoIzq = Math.sqrt(Math.pow(x - 35, 2) + Math.pow(y - 38, 2));
                double distOjoDer = Math.sqrt(Math.pow(x - 65, 2) + Math.pow(y - 38, 2));
                double distSonrisa = Math.sqrt(Math.pow(x - 50, 2) + Math.pow(y - 55, 2));

                Pixel pixel;

                if (distCara > 45) {
                    pixel = new Pixel(255, 255, 255);          // fondo blanco
                } else if (distCara > 42) {
                    pixel = new Pixel(0, 0, 0);                // contorno negro
                } else if (distOjoIzq < 6 || distOjoDer < 6) {
                    pixel = new Pixel(0, 0, 0);                // ojos
                } else if (distSonrisa > 26 && distSonrisa < 31 && y > 58) {
                    pixel = new Pixel(0, 0, 0);                // sonrisa (arco inferior)
                } else {
                    pixel = new Pixel(255, 220, 0);            // cara amarilla
                }

                image.addPixel(pixel);
            }
        }

        ImageVisualizer visualizer = new ImageVisualizer();
        visualizer.showImage(image);
    }
}