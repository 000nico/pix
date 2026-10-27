package Classes;

import java.time.LocalDateTime;
import java.util.Set;

public class Obra {
    private int id;
    private int idArtista;
    private String titulo;
    private Image imagen;
    private Set<String> tags;
    private LocalDateTime fechaPublicacion;
    private boolean activo;

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

    public boolean isActivo() {return activo;}
    public void setActivo(boolean activo) {this.activo = activo;}

    public Set<String> getTags() {return tags;}
    public void setTags(Set<String> tags) {this.tags = tags;}

    //constructor
    public Obra() {
    }
    public Obra(int id, int idArtista, String titulo, Image imagen, Set<String> tags, LocalDateTime fechaPublicacion, boolean activo) {
        this.id = id;
        this.idArtista = idArtista;
        this.titulo = titulo;
        this.imagen = imagen;
        this.tags = tags;
        this.fechaPublicacion = fechaPublicacion;
        this.activo = activo;
    }


}
