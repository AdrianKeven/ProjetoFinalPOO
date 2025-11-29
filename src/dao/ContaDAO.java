/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entidades.Conta;
import java.sql.SQLException;
import java.util.List;

public interface ContaDAO {
    void salvar(Conta conta) throws SQLException;
    void atualizar(Conta conta) throws SQLException;
    void deletar(String numeroConta) throws SQLException;
    Conta buscarPorNumero(String numero) throws SQLException;
    List<Conta> listarPorCliente(String cpfCliente) throws SQLException;
    List<Conta> listarTodas() throws SQLException;
}
