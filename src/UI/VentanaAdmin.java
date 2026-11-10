package UI;

import Classes.Publicacion.Coleccion;
import Classes.Publicacion.Comentario;
import Classes.Users.User;
import DataBase.Service.ColeccionService;
import DataBase.Service.ComentarioService;
import DataBase.Service.PublicacionService;
import DataBase.Service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

// panel del administrador: estadisticas, listados y moderacion
public class VentanaAdmin extends JFrame {
    private User admin;
    private JTextArea area;

    public VentanaAdmin(User admin) {
        this.admin = admin;
        setTitle("pix - panel de administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        area = new JTextArea(18, 62);
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(4, 2, 5, 5));

        JButton botonEstadisticas = new JButton("estadisticas de usuarios");
        botonEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                estadisticas();
            }
        });
        botones.add(botonEstadisticas);

        JButton botonUsuarios = new JButton("listar usuarios");
        botonUsuarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarUsuarios();
            }
        });
        botones.add(botonUsuarios);

        JButton botonComentarios = new JButton("listar comentarios");
        botonComentarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarComentarios();
            }
        });
        botones.add(botonComentarios);

        JButton botonBorrarComentario = new JButton("borrar comentario");
        botonBorrarComentario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                borrarComentario();
            }
        });
        botones.add(botonBorrarComentario);

        JButton botonColecciones = new JButton("listar colecciones");
        botonColecciones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarColecciones();
            }
        });
        botones.add(botonColecciones);

        JButton botonReactivarObra = new JButton("reactivar obra");
        botonReactivarObra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reactivarObra();
            }
        });
        botones.add(botonReactivarObra);

        JButton botonUsuariosBaja = new JButton("dar de baja / reactivar usuario");
        botonUsuariosBaja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bajaYReactivarUsuario();
            }
        });
        botones.add(botonUsuariosBaja);

        JButton botonCrearAdmin = new JButton("crear administrador");
        botonCrearAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearAdministrador();
            }
        });
        botones.add(botonCrearAdmin);

        add(botones, BorderLayout.SOUTH);

        setSize(760, 460);
        setLocationRelativeTo(null);
    }

    // muestra el map usuario -> cantidad de obras que arma la gestora de usuarios
    private void estadisticas() {
        try {
            UsuarioService usuarios = new UsuarioService();
            Map<User, Integer> mapa = usuarios.contarObrasPorUsuario(admin);

            String texto = "";
            for (Map.Entry<User, Integer> entrada : mapa.entrySet()) {
                texto = texto + entrada.getKey().getNombre() + " (" + entrada.getKey().getRol() + ") -> " + entrada.getValue() + " obras\n";
            }
            area.setText(texto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // lista los usuarios activos de la base
    private void listarUsuarios() {
        try {
            UsuarioService usuarios = new UsuarioService();
            List<User> lista = usuarios.listar(admin);

            String texto = "";
            for (int i = 0; i < lista.size(); i++) {
                User u = lista.get(i);
                texto = texto + "id: " + u.getId() + " | " + u.getNombre() + " | " + u.getEmail() + " | " + u.getRol() + " | registrado: " + u.getFechaRegistro() + "\n";
            }
            area.setText(texto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // lista todos los comentarios activos para poder moderarlos
    private void listarComentarios() {
        try {
            ComentarioService comentarios = new ComentarioService();
            List<Comentario> lista = comentarios.listarTodos(admin);

            String texto = "";
            for (int i = 0; i < lista.size(); i++) {
                Comentario c = lista.get(i);
                texto = texto + "id: " + c.getId() + " | usuario: " + c.getId_usuario() + " | obra: " + c.getIdObra() + " | " + c.getFecha() + " | " + c.getContent() + "\n";
            }
            if (texto.isEmpty()) {
                texto = "no hay comentarios activos";
            }
            area.setText(texto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // borra un comentario (baja logica)
    private void borrarComentario() {
        int idComentario = pedirNumero("id del comentario a borrar:");
        if (idComentario < 0) {
            return;
        }
        try {
            ComentarioService comentarios = new ComentarioService();
            comentarios.eliminar(admin, idComentario);
            JOptionPane.showMessageDialog(this, "comentario borrado");
            listarComentarios();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // lista todas las colecciones activas de todos los usuarios
    private void listarColecciones() {
        try {
            ColeccionService colecciones = new ColeccionService();
            List<Coleccion> lista = colecciones.listarTodas(admin);

            String texto = "";
            for (int i = 0; i < lista.size(); i++) {
                Coleccion c = lista.get(i);
                texto = texto + "id: " + c.getId() + " | duenio: " + c.getIdUsuario() + " | " + c.getNombre() + " | " + c.getDescripcion() + " | obras: " + c.getIdsObras() + "\n";
            }
            if (texto.isEmpty()) {
                texto = "no hay colecciones activas";
            }
            area.setText(texto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // deshace la baja logica de una obra
    private void reactivarObra() {
        int idPublicacion = pedirNumero("id de la publicacion a reactivar:");
        if (idPublicacion < 0) {
            return;
        }
        try {
            PublicacionService publicaciones = new PublicacionService();
            publicaciones.reactivar(admin, idPublicacion);
            JOptionPane.showMessageDialog(this, "obra reactivada");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // baja logica o reactivacion de un usuario
    private void bajaYReactivarUsuario() {
        int idUsuario = pedirNumero("id del usuario:");
        if (idUsuario < 0) {
            return;
        }
        String[] opciones = { "dar de baja", "reactivar" };
        int eleccion = JOptionPane.showOptionDialog(this, "que queres hacer con el usuario " + idUsuario + "?", "usuarios", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        try {
            UsuarioService usuarios = new UsuarioService();
            if (eleccion == 0) {
                usuarios.darDeBaja(admin, idUsuario);
                JOptionPane.showMessageDialog(this, "usuario dado de baja");
            } else if (eleccion == 1) {
                usuarios.reactivar(admin, idUsuario);
                JOptionPane.showMessageDialog(this, "usuario reactivado");
            }
            listarUsuarios();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // crea otro administrador
    private void crearAdministrador() {
        String nombre = JOptionPane.showInputDialog(this, "nombre:");
        String email = JOptionPane.showInputDialog(this, "email:");
        String password = JOptionPane.showInputDialog(this, "password (minimo 4 caracteres):");
        String nivel = JOptionPane.showInputDialog(this, "nivel de acceso (numero):");

        if (nombre == null || email == null || password == null || nivel == null) {
            return;
        }
        try {
            UsuarioService usuarios = new UsuarioService();
            usuarios.crearAdministrador(admin, nombre, email, password, Integer.parseInt(nivel.trim()));
            JOptionPane.showMessageDialog(this, "administrador creado");
            listarUsuarios();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "el nivel de acceso tiene que ser un numero", "error", JOptionPane.WARNING_MESSAGE);
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
