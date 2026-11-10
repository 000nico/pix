import UI.VentanaLogin;

// arranque de la app: abre el login, que despues lleva al menu segun el rol del usuario
public class Main {
    public static void main(String[] args) {
        new VentanaLogin().setVisible(true);
    }
}
