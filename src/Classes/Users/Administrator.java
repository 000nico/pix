package Classes.Users;

import Enum.RolUsuario;
public class Administrator extends User{

    private int nivelAcceso;


    //getters y setters

    public int getNivelAcceso() {return nivelAcceso;}
    public void setNivelAcceso(int nivelAcceso) {this.nivelAcceso = nivelAcceso;}

    //constructores

    public Administrator() {
    }

    public Administrator(int id, String nombre, String email, String password, RolUsuario rol) {
        super(id, nombre, email, password, rol);
    }
    //

}
