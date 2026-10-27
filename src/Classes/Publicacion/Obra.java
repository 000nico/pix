package Classes.Publicacion;

import Classes.Image.Image;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class Obra {
    private int id;
    private int idArtista;
    private String titulo;
    private String descripcion;
    private Image imagen;
    private LocalDateTime fechaPublicacion;
    private Queue<Comentario> comentarios;

    //getters y setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getIdArtista() {return idArtista;}
    public void setIdArtista(int idArtista) {this.idArtista = idArtista;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public Image getImagen() {return imagen;}
    public void setImagen(Image imagen) {this.imagen = imagen;}

    public LocalDateTime getFechaPublicacion() {return fechaPublicacion;}
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {this.fechaPublicacion = fechaPublicacion;}

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Queue<Comentario> getComentarios() { return comentarios; }
    public void addComentario(Comentario comentario) { comentarios.add(comentario); }

    //constructor
    public Obra() { }
    public Obra(int id, int idArtista, String titulo, String descripcion, Image imagen, Set<String> tags, LocalDateTime fechaPublicacion, boolean activo) {
        this.id = id;
        this.idArtista = idArtista;
        this.titulo = titulo;
        this.imagen = imagen;
        this.fechaPublicacion = fechaPublicacion;
        this.descripcion = descripcion;
        this.comentarios = new LinkedList<>();
    }
}
