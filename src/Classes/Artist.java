package Classes;



public class Artist {

    private String bio;
    //private List<Obra> obras;  deberia ir en la clase gestora


    //getters y setters

    public String getBio() {return bio;}
    public void setBio(String bio) {this.bio = bio;}

    //constructor

    public Artist() {
    }
    public Artist(int id, String nombre, String email, String password, String bio) {
        //super(id, nombre, email, password);
        this.bio = bio;
    }

    //

}
