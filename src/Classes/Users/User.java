package Classes.Users;

import Enum.RolUsuario;

public abstract class User {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private RolUsuario rol;
    //setters y getters

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    //constructores

    public User() {
    }

    public User(int id, String nombre, String email, String password, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    //

}
