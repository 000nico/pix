package Classes.Users;

import Enum.RolUsuario;

// artista: es el unico usuario que puede publicar obras, tiene una bio
public class Artist extends User {

    private String bio;

    //constructores
    public Artist() { }

    public Artist(int id, String nombre, String email, String password, RolUsuario rol, String bio) {
        super(id, nombre, email, password, rol);
        this.bio = bio;
    }

    //getters y setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
