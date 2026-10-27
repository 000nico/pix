package Classes.Publicacion;

public class Comentario {
    private String content;
    private int id_usuario;

    public Comentario(int id_usuario, String content) {
        this.id_usuario = id_usuario;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
