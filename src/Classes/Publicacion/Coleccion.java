package Classes.Publicacion;

import java.util.ArrayList;
import java.util.List;


 // Colección (álbum) que arma un usuario para guardar obras.
 //Guarda los ids de las obras, no los objetos, para no cargar todas las imágenes.

public class Coleccion {
    private int id;
    private int idUsuario;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<Integer> idsObras;

    //contructores
    public Coleccion() {
        this.idsObras = new ArrayList<>();
        this.activo = true;
    }

    // Para crear una colección nueva (el id lo genera la base)
    public Coleccion(int idUsuario, String nombre, String descripcion) {
        this();
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Para armar una colección leída de la base
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
