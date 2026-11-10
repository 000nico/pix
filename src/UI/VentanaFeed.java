package UI;

import Classes.Image.ImageVisualizer;
import Classes.Publicacion.Coleccion;
import Classes.Publicacion.Comentario;
import Classes.Publicacion.Publicacion;
import Classes.Users.User;
import DataBase.Service.ColeccionService;
import DataBase.Service.ComentarioService;
import DataBase.Service.PublicacionService;
import DataBase.Service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// feed del catalogo: lista las publicaciones activas y deja verlas, comentarlas y guardarlas en colecciones
public class VentanaFeed extends JFrame {
    private User user;
    private boolean soloMias;
    private JTextArea area;
    private List<Publicacion> publicaciones;

    // con solomias en true solo muestra las obras del usuario logueado
    public VentanaFeed(User user, boolean soloMias) {
        this.user = user;
        this.soloMias = soloMias;
        if (soloMias) {
            setTitle("pix - mis obras");
        } else {
            setTitle("pix - feed");
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        area = new JTextArea(16, 62);
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(5, 2, 5, 5));

        JButton botonActualizar = new JButton("actualizar");
        botonActualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarPublicaciones();
            }
        });
        botones.add(botonActualizar);

        JButton botonBuscarTag = new JButton("buscar por tag");
        botonBuscarTag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPorTag();
            }
        });
        botones.add(botonBuscarTag);

        JButton botonVer = new JButton("ver imagen");
        botonVer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verImagen();
            }
        });
        botones.add(botonVer);

        JButton botonComentarios = new JButton("ver comentarios");
        botonComentarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verComentarios();
            }
        });
        botones.add(botonComentarios);

        JButton botonComentar = new JButton("comentar");
        botonComentar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comentar();
            }
        });
        botones.add(botonComentar);

        JButton botonEditarComentario = new JButton("editar comentario");
        botonEditarComentario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarComentario();
            }
        });
        botones.add(botonEditarComentario);

        JButton botonBorrarComentario = new JButton("borrar comentario");
        botonBorrarComentario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                borrarComentario();
            }
        });
        botones.add(botonBorrarComentario);

        JButton botonGuardar = new JButton("guardar en coleccion");
        botonGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarEnColeccion();
            }
        });
        botones.add(botonGuardar);

        JButton botonEditarObra = new JButton("editar obra");
        botonEditarObra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarObra();
            }
        });
        botones.add(botonEditarObra);

        JButton botonBaja = new JButton("dar de baja obra");
        botonBaja.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                darDeBajaObra();
            }
        });
        botones.add(botonBaja);

        add(botones, BorderLayout.SOUTH);

        cargarPublicaciones();
        pack();
        setLocationRelativeTo(null);
    }

    // trae las publicaciones de la base y las escribe en el area
    private void cargarPublicaciones() {
        try {
            PublicacionService publicaciones = new PublicacionService();
            if (soloMias) {
                this.publicaciones = publicaciones.listarPorArtista(user.getId());
            } else {
                this.publicaciones = publicaciones.verFeed();
            }
            escribirLista();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // escribe la lista de publicaciones en el area de texto
    private void escribirLista() {
        String texto = "";
        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion pub = publicaciones.get(i);
            texto = texto + "id: " + pub.getId() + " | " + pub.getObra().getTitulo() + " | artista: " + pub.getObra().getIdArtista() + " | " + pub.getObra().getFechaPublicacion() + " | tags: " + pub.getTags() + "\n";
        }
        if (texto.isEmpty()) {
            texto = "no hay publicaciones para mostrar";
        }
        area.setText(texto);
    }

    // busca en la base por hashtag y muestra el resultado en el area
    private void buscarPorTag() {
        String tag = JOptionPane.showInputDialog(this, "tag a buscar (con o sin #):");
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        try {
            PublicacionService publicaciones = new PublicacionService();
            this.publicaciones = publicaciones.buscarPorTag(tag);
            escribirLista();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // muestra la imagen de la obra con el imagevisualizer
    private void verImagen() {
        Publicacion pub = pedirPublicacion("id de la publicacion a ver:");
        if (pub == null) {
            return;
        }
        new ImageVisualizer().showImage(pub.getObra().getImagen());
    }

    // lista los comentarios de una publicacion
    private void verComentarios() {
        Publicacion pub = pedirPublicacion("id de la publicacion:");
        if (pub == null) {
            return;
        }
        try {
            ComentarioService comentarios = new ComentarioService();
            UsuarioService usuarios = new UsuarioService();
            List<Comentario> lista = comentarios.listarPorPublicacion(pub.getId());

            String texto = "";
            for (int i = 0; i < lista.size(); i++) {
                Comentario c = lista.get(i);
                String nombre = "usuario #" + c.getId_usuario();
                // busco el nombre del que comento, si ya fue dado de baja muestro el id
                try {
                    nombre = usuarios.buscarPorId(c.getId_usuario()).getNombre();
                } catch (Exception e) {
                    nombre = "usuario #" + c.getId_usuario();
                }
                texto = texto + "id " + c.getId() + " | " + nombre + " (" + c.getFecha() + "): " + c.getContent() + "\n";
            }
            if (texto.isEmpty()) {
                texto = "esta obra no tiene comentarios";
            }
            JOptionPane.showMessageDialog(this, texto, "comentarios", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // deja un comentario en una publicacion
    private void comentar() {
        Publicacion pub = pedirPublicacion("id de la publicacion a comentar:");
        if (pub == null) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "tu comentario:");
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        try {
            ComentarioService comentarios = new ComentarioService();
            Comentario c = comentarios.comentar(user, pub.getId(), texto);
            JOptionPane.showMessageDialog(this, "comentario guardado con id " + c.getId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // cambia el texto de un comentario propio
    private void editarComentario() {
        int idComentario = pedirNumero("id del comentario a editar:");
        if (idComentario < 0) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "nuevo texto del comentario:");
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        try {
            ComentarioService comentarios = new ComentarioService();
            comentarios.editar(user, idComentario, texto);
            JOptionPane.showMessageDialog(this, "comentario editado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // borra un comentario propio, o cualquiera si es el administrador
    private void borrarComentario() {
        int idComentario = pedirNumero("id del comentario a borrar:");
        if (idComentario < 0) {
            return;
        }
        try {
            ComentarioService comentarios = new ComentarioService();
            comentarios.eliminar(user, idComentario);
            JOptionPane.showMessageDialog(this, "comentario borrado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // guarda una publicacion en una coleccion propia
    private void guardarEnColeccion() {
        Publicacion pub = pedirPublicacion("id de la publicacion a guardar:");
        if (pub == null) {
            return;
        }
        try {
            ColeccionService colecciones = new ColeccionService();
            List<Coleccion> mias = colecciones.listarPorUsuario(user.getId());
            if (mias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "todavia no tenes colecciones, crea una primero", "colecciones", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String texto = "";
            for (int i = 0; i < mias.size(); i++) {
                texto = texto + "id: " + mias.get(i).getId() + " | " + mias.get(i).getNombre() + " | obras: " + mias.get(i).getIdsObras() + "\n";
            }
            JOptionPane.showMessageDialog(this, texto, "tus colecciones", JOptionPane.INFORMATION_MESSAGE);

            int idColeccion = pedirNumero("id de la coleccion donde guardar:");
            if (idColeccion < 0) {
                return;
            }
            colecciones.agregarObra(user, idColeccion, pub.getId());
            JOptionPane.showMessageDialog(this, "obra guardada en la coleccion");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // cambia titulo, descripcion y tags de una obra propia o de cualquiera si es el administrador
    private void editarObra() {
        Publicacion pub = pedirPublicacion("id de la publicacion a editar:");
        if (pub == null) {
            return;
        }
        String titulo = JOptionPane.showInputDialog(this, "titulo nuevo:", pub.getObra().getTitulo());
        if (titulo == null || titulo.trim().isEmpty()) {
            return;
        }
        String descripcion = JOptionPane.showInputDialog(this, "descripcion nueva:", pub.getObra().getDescripcion());
        String tags = JOptionPane.showInputDialog(this, "tags nuevos (separados por coma):", pub.getTags());

        try {
            PublicacionService publicaciones = new PublicacionService();
            publicaciones.editar(user, pub.getId(), titulo, descripcion, armarTags(tags));
            cargarPublicaciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // baja logica de la obra. solo el duenio o el administrador
    private void darDeBajaObra() {
        Publicacion pub = pedirPublicacion("id de la publicacion a dar de baja:");
        if (pub == null) {
            return;
        }
        try {
            PublicacionService publicaciones = new PublicacionService();
            publicaciones.darDeBaja(user, pub.getId());
            JOptionPane.showMessageDialog(this, "obra dada de baja");
            cargarPublicaciones();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // auxiliares

    // pide un id y busca la publicacion en la lista que se esta mostrando
    private Publicacion pedirPublicacion(String mensaje) {
        int id = pedirNumero(mensaje);
        if (id < 0) {
            JOptionPane.showMessageDialog(this, "tenes que escribir un numero", "feed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        for (int i = 0; i < publicaciones.size(); i++) {
            if (publicaciones.get(i).getId() == id) {
                return publicaciones.get(i);
            }
        }
        JOptionPane.showMessageDialog(this, "no encontre esa publicacion en la lista", "feed", JOptionPane.WARNING_MESSAGE);
        return null;
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
