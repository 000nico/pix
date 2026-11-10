package Classes.Users;

import Enum.RolUsuario;

import java.time.LocalDate;

public abstract class User {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private RolUsuario rol;
    private boolean activo;
    private LocalDate fechaRegistro;

    //setters y getters

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public RolUsuario getRol() {return rol;}
    public void setRol(RolUsuario rol) {this.rol = rol;}

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    //constructores

    public User() {
    }
    public User(int id, String nombre, String email, String password, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
        this.fechaRegistro = LocalDate.now();
    }
    //

}
