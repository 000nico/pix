package Classes.Users;

import Enum.RolUsuario;

// administrador: no publica obras pero puede moderar y ver estadisticas, tiene nivel de acceso
public class Administrator extends User {

    private int nivelAcceso;

    //constructores
    public Administrator() { }

    public Administrator(int id, String nombre, String email, String password, RolUsuario rol) {
        super(id, nombre, email, password, rol);
    }

    //getters y setters
    public int getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(int nivelAcceso) { this.nivelAcceso = nivelAcceso; }
}
