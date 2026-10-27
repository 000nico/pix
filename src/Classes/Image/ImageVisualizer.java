package Classes.Image;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageVisualizer {
    public void showImage(Image image) {
        BufferedImage buffered_image = new BufferedImage(image.getHeader().getWidth(), image.getHeader().getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < image.getHeader().getWidth(); x++) {
            for(int y = 0; y < image.getHeader().getHeight(); y++) {
                Pixel actual = image.getPixel(x, y);

                int color = actual.getR() << 16 | actual.getG()  << 8 | actual.getB();

                buffered_image.setRGB(x, y, color);
            }
        }

        ImageIcon icon = new ImageIcon(buffered_image);
        JLabel jlabel = new JLabel(icon);

        JFrame jframe = new JFrame("Image Visualizer");
        jframe.add(jlabel);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }
}
