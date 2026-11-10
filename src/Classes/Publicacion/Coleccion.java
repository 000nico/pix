package Classes.Publicacion;

import java.util.ArrayList;
import java.util.List;

// coleccion (album) que arma un usuario para guardar obras. guarda los ids de las obras, no los objetos, para no cargar todas las imagenes
public class Coleccion {
    private int id;
    private int idUsuario;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<Integer> idsObras;

    //constructores
    public Coleccion() {
        this.idsObras = new ArrayList<Integer>();
        this.activo = true;
    }

    // para crear una coleccion nueva, el id lo genera la base
    public Coleccion(int idUsuario, String nombre, String descripcion) {
        this();
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // para armar una coleccion leida de la base
    public Coleccion(int id, int idUsuario, String nombre, String descripcion, boolean activo) {
        this(idUsuario, nombre, descripcion);
        this.id = id;
        this.activo = activo;
    }

    //getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Integer> getIdsObras() { return idsObras; }
    public void setIdsObras(List<Integer> idsObras) { this.idsObras = idsObras; }
}
