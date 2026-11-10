package UI;

import Classes.Image.Header;
import Classes.Image.Image;
import Classes.Image.Pixel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// editor de pixel art. se usa para dibujar la imagen de una obra nueva antes de publicarla
public class ImageEditor extends JFrame {
    private Image imageModel;
    private Color currentColor = Color.BLACK;
    private final int pixelSize = 20;

    public ImageEditor(int width, int height) {
        Header header = new Header(width, height);
        this.imageModel = new Image(header);

        // arranca con todos los pixeles en blanco
        for (int i = 0; i < width * height; i++) {
            this.imageModel.addPixel(new Pixel(255, 255, 255));
        }

        setTitle("editor pixel art");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        CanvasPanel canvasPanel = new CanvasPanel();
        add(canvasPanel, BorderLayout.CENTER);

        // panel de paleta de colores
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        Color[] palette = { Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.WHITE, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, new Color(128, 0, 128) };

        for (int i = 0; i < palette.length; i++) {
            final Color c = palette[i];
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(24, 24));
            btn.setBackground(c);
            btn.setFocusPainted(false);
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    currentColor = c;
                }
            });
            controlPanel.add(btn);
        }

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // devuelve la imagen tal como quedo dibujada, la usa la ventana de publicar
    public Image getImagen() {
        return imageModel;
    }

    // canvas donde se dibuja haciendo click o arrastrando el mouse
    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            int w = imageModel.getHeader().getWidth() * pixelSize;
            int h = imageModel.getHeader().getHeight() * pixelSize;
            setPreferredSize(new Dimension(w, h));

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    paintPixelAt(e.getX(), e.getY());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    paintPixelAt(e.getX(), e.getY());
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        // pinta el pixel que queda debajo del mouse
        private void paintPixelAt(int mouseX, int mouseY) {
            int x = mouseX / pixelSize;
            int y = mouseY / pixelSize;

            int width = imageModel.getHeader().getWidth();
            int height = imageModel.getHeader().getHeight();

            if (x >= 0 && x < width && y >= 0 && y < height) {
                Pixel newPixel = new Pixel(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
                imageModel.setPixel(newPixel, x, y);
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = imageModel.getHeader().getWidth();
            int height = imageModel.getHeader().getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Pixel p = imageModel.getPixel(x, y);
                    g.setColor(new Color(p.getR(), p.getG(), p.getB()));
                    g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);

                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }
}
