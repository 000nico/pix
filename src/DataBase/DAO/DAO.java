package DataBase.DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    void insertar(T objeto) throws SQLException;
    Optional<T> buscarPorId(int id) throws SQLException;
    List<T> listar() throws SQLException;
    void actualizar(T objeto) throws SQLException;
    void bajaLogica(int id) throws SQLException;
}
