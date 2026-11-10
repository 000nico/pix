package UI;

import Classes.Publicacion.Coleccion;
import Classes.Users.User;
import DataBase.Service.ColeccionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// colecciones del usuario: crearlas, editarlas, agregar y sacar obras
public class VentanaColecciones extends JFrame {
    private User user;
    private JTextArea area;

    public VentanaColecciones(User user) {
        this.user = user;
        setTitle("pix - mis colecciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        area = new JTextArea(14, 50);
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(3, 2, 5, 5));

        JButton botonCrear = new JButton("crear coleccion");
        botonCrear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crear();
            }
        });
        botones.add(botonCrear);

        JButton botonAgregar = new JButton("agregar obra");
        botonAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarObra();
            }
        });
        botones.add(botonAgregar);

        JButton botonQuitar = new JButton("quitar obra");
        botonQuitar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quitarObra();
            }
        });
        botones.add(botonQuitar);

        JButton botonEditar = new JButton("editar coleccion");
        botonEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        });
        botones.add(botonEditar);

        JButton botonBaja = new JButton("dar de baja coleccion");
        botonBaja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                darDeBaja();
            }
        });
        botones.add(botonBaja);

        JButton botonActualizar = new JButton("actualizar lista");
        botonActualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarColecciones();
            }
        });
        botones.add(botonActualizar);

        add(botones, BorderLayout.SOUTH);

        cargarColecciones();
        pack();
        setLocationRelativeTo(null);
    }

    // trae las colecciones del usuario de la base y las escribe en el area
    private void cargarColecciones() {
        try {
            ColeccionService colecciones = new ColeccionService();
            List<Coleccion> lista = colecciones.listarPorUsuario(user.getId());

            String texto = "";
            for (int i = 0; i < lista.size(); i++) {
                Coleccion c = lista.get(i);
                texto = texto + "id: " + c.getId() + " | " + c.getNombre() + " | " + c.getDescripcion() + " | obras: " + c.getIdsObras() + "\n";
            }
            if (texto.isEmpty()) {
                texto = "todavia no tenes colecciones";
            }
            area.setText(texto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // crea una coleccion nueva pidiendo nombre y descripcion
    private void crear() {
        String nombre = JOptionPane.showInputDialog(this, "nombre de la coleccion:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        String descripcion = JOptionPane.showInputDialog(this, "descripcion (puede quedar vacia):");

        try {
            ColeccionService colecciones = new ColeccionService();
            Coleccion c = colecciones.crear(user, nombre, descripcion);
            JOptionPane.showMessageDialog(this, "coleccion creada con id " + c.getId());
            cargarColecciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // suma una publicacion a una coleccion propia
    private void agregarObra() {
        int idColeccion = pedirNumero("id de la coleccion:");
        if (idColeccion < 0) {
            return;
        }
        int idPublicacion = pedirNumero("id de la publicacion a agregar (lo ves en el feed):");
        if (idPublicacion < 0) {
            return;
        }
        try {
            ColeccionService colecciones = new ColeccionService();
            colecciones.agregarObra(user, idColeccion, idPublicacion);
            JOptionPane.showMessageDialog(this, "obra agregada a la coleccion");
            cargarColecciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // saca una publicacion de una coleccion propia
    private void quitarObra() {
        int idColeccion = pedirNumero("id de la coleccion:");
        if (idColeccion < 0) {
            return;
        }
        int idPublicacion = pedirNumero("id de la publicacion a quitar:");
        if (idPublicacion < 0) {
            return;
        }
        try {
            ColeccionService colecciones = new ColeccionService();
            colecciones.quitarObra(user, idColeccion, idPublicacion);
            JOptionPane.showMessageDialog(this, "obra quitada de la coleccion");
            cargarColecciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // cambia nombre y descripcion de una coleccion propia
    private void editar() {
        int idColeccion = pedirNumero("id de la coleccion a editar:");
        if (idColeccion < 0) {
            return;
        }
        String nombre = JOptionPane.showInputDialog(this, "nombre nuevo:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        String descripcion = JOptionPane.showInputDialog(this, "descripcion nueva:");

        try {
            ColeccionService colecciones = new ColeccionService();
            colecciones.editar(user, idColeccion, nombre, descripcion);
            cargarColecciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // baja logica de una coleccion propia
    private void darDeBaja() {
        int idColeccion = pedirNumero("id de la coleccion a dar de baja:");
        if (idColeccion < 0) {
            return;
        }
        try {
            ColeccionService colecciones = new ColeccionService();
            colecciones.darDeBaja(user, idColeccion);
            JOptionPane.showMessageDialog(this, "coleccion dada de baja");
            cargarColecciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // pide un numero por pantalla, devuelve -1 si lo escrito no es un numero
    private int pedirNumero(String mensaje) {
        String texto = JOptionPane.showInputDialog(this, mensaje);
        if (texto == null) {
            return -1;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
