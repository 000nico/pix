package Classes.Publicacion;

import java.time.LocalDateTime;

public class Comentario {
    private int id;
    private int idObra;
    private int id_usuario;
    private String content;
    private LocalDateTime fecha;
    private boolean activo;

    //constructores
    public Comentario() { }

    public Comentario(int id_usuario, String content) {
        this.id_usuario = id_usuario;
        this.content = content;
        this.fecha = LocalDateTime.now();
        this.activo = true;
    }
    //  para comentar una obra puntual
    public Comentario(int idObra, int id_usuario, String content) {
        this(id_usuario, content);
        this.idObra = idObra;
    }

    //getters y setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdObra() { return idObra; }
    public void setIdObra(int idObra) { this.idObra = idObra; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}