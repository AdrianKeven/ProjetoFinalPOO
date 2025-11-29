package dao;

import entidades.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {
    void salvar(Cliente cliente) throws SQLException;
    void atualizar(Cliente cliente) throws SQLException;
    void deletar(String cpf) throws SQLException;
    Cliente buscarPorCpf(String cpf) throws SQLException;
    List<Cliente> listarTodos() throws SQLException;
}

