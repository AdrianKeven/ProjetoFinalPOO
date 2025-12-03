package dao;

import entidades.Cliente;
import util.DBConnection;
import utilitarios.ClienteNaoEncontradoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteDAOJdbc implements ClienteDAO {

    @Override
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Cliente (cpf, nome, endereco) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getCpf());
            ps.setString(2, cliente.getNome());
            ps.setString(3, cliente.getEndereco());
            ps.executeUpdate();
        }
    }

    @Override
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE Cliente SET nome = ?, endereco = ? WHERE cpf = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            ps.setString(3, cliente.getCpf());
            ps.executeUpdate();
        }
    }

    @Override
    public void deletar(String cpf) throws SQLException, ClienteNaoEncontradoException {
        String sql = "DELETE FROM Cliente WHERE cpf = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);
            int afetados = ps.executeUpdate();

            if (afetados == 0) {
                throw new ClienteNaoEncontradoException("Cliente não existe no banco de dados");
            }
        }
    }


    @Override
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT cpf, nome, endereco FROM Cliente WHERE cpf = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                );
            }
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT cpf, nome, endereco FROM Cliente";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                ));
            }
        }
        return lista;
    }

    // Método para atualizar o HashMap de clientes
    public void atualizarClientesMap(Map<String, Cliente> clientes) {
        clientes.clear();

        String sql = "SELECT nome, cpf, endereco FROM Cliente";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                );

                clientes.put(c.getCpf(), c);
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar clientes: " + e.getMessage());
        }
    }
}
