/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package projetofinal.dao;

import projetofinal.entidades.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {
    void salvar(Cliente cliente) throws SQLException;
    void atualizar(Cliente cliente) throws SQLException;
    void deletar(String cpf) throws SQLException;
    Cliente buscarPorCpf(String cpf) throws SQLException;
    List<Cliente> listarTodos() throws SQLException;
}

