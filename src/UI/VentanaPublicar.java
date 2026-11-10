package UI;

import Classes.Publicacion.Publicacion;
import Classes.Users.User;
import DataBase.Service.PublicacionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

// ventana para publicar una obra nueva: primero se dibuja la imagen en el editor y despues se guarda en la base
public class VentanaPublicar extends JFrame {
    private User user;
    private JTextField campoTitulo;
    private JTextField campoTags;
    private JTextArea areaDescripcion;
    private ImageEditor editor;

    public VentanaPublicar(User user) {
        this.user = user;
        setTitle("pix - publicar obra");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel arriba = new JPanel(new GridLayout(3, 2, 10, 10));
        arriba.add(new JLabel("titulo:"));
        campoTitulo = new JTextField();
        arriba.add(campoTitulo);

        arriba.add(new JLabel("tags (separados por coma):"));
        campoTags = new JTextField();
        arriba.add(campoTags);

        arriba.add(new JLabel("descripcion:"));
        areaDescripcion = new JTextArea(4, 20);
        arriba.add(new JScrollPane(areaDescripcion));
        add(arriba, BorderLayout.NORTH);

        JPanel abajo = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton botonDibujar = new JButton("dibujar la imagen");
        botonDibujar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dibujar();
            }
        });
        abajo.add(botonDibujar);

        JButton botonPublicar = new JButton("publicar");
        botonPublicar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                publicar();
            }
        });
        abajo.add(botonPublicar);
        add(abajo, BorderLayout.SOUTH);

        setSize(430, 320);
        setLocationRelativeTo(null);
    }

    // abre el editor de pixel art. la imagen queda guardada en el editor hasta que se publica
    private void dibujar() {
        if (editor == null) {
            editor = new ImageEditor(16, 16);
            editor.setVisible(true);
        } else {
            editor.toFront();
        }
    }

    // guarda la obra en la base con la imagen que quedo dibujada en el editor
    private void publicar() {
        String titulo = campoTitulo.getText().trim();
        String descripcion = areaDescripcion.getText();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "el titulo es obligatorio", "publicar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (editor == null) {
            JOptionPane.showMessageDialog(this, "primero dibuja la imagen", "publicar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PublicacionService publicaciones = new PublicacionService();
            Publicacion pub = publicaciones.publicar(user, titulo, descripcion, editor.getImagen(), armarTags(campoTags.getText()));
            JOptionPane.showMessageDialog(this, "obra publicada con id " + pub.getId());
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // pasa el texto de los tags a un set, separando por coma
    private Set<String> armarTags(String texto) {
        Set<String> tags = new HashSet<String>();
        if (texto == null || texto.trim().isEmpty()) {
            return tags;
        }
        String[] partes = texto.split(",");
        for (int i = 0; i < partes.length; i++) {
            if (!partes[i].trim().isEmpty()) {
                tags.add(partes[i].trim());
            }
        }
        return tags;
    }
}
