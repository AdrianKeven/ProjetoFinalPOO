package dao;

import entidades.Cliente;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJdbc implements ClienteDAO {

    @Override
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (cpf, nome, endereco) VALUES (?, ?, ?)";
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
        String sql = "UPDATE clientes SET nome = ?, endereco = ? WHERE cpf = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            ps.setString(3, cliente.getCpf());
            ps.executeUpdate();
        }
    }

    @Override
    public void deletar(String cpf) throws SQLException {
        String sql = "DELETE FROM clientes WHERE cpf = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);
            ps.executeUpdate();
        }
    }

    @Override
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT cpf, nome, endereco FROM clientes WHERE cpf = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("endereco")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM clientes";
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
}

