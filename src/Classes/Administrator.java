package Classes;

public class Administrator {

    private int nivelAcceso;


    //getters y setters

    public int getNivelAcceso() {return nivelAcceso;}
    public void setNivelAcceso(int nivelAcceso) {this.nivelAcceso = nivelAcceso;}

    //constructores

    public Administrator() {
    }
    public Administrator(int id, String nombre, String email, String password, int nivelAcceso) {
        //super(id, nombre, email, password);
        this.nivelAcceso = nivelAcceso;
    }

    //

}
