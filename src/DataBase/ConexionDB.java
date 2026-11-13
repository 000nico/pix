package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// conexion con la base mysql "pix". todos los dao la usan
public class ConexionDB {
    private static final String URL = "jdbc:mysql://172.16.1.64:3306/pix";
    private static final String USER = "bdd1";
    private static final String PASSWORD = "bdd1";

    // abre una conexion nueva cada vez que se la piden
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("no se encontro el driver de mysql", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
