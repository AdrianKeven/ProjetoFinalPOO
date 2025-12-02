package dao;

import entidades.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContaDAOJdbc implements ContaDAO {

    private final ClienteDAO clienteDAO = new ClienteDAOJdbc();

    // -----------------------------
    // SALVAR CONTA
    // -----------------------------
    @Override
    public void salvar(Conta conta) throws SQLException {
        String sql = "INSERT INTO Conta (numero, tipo, saldo, proprietario_cpf) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conta.getNumero());
            ps.setString(2, conta instanceof ContaCorrente ? "corrente" : "poupanca");
            ps.setDouble(3, conta.getSaldo());
            ps.setString(4, conta.getProprietario().getCpf());

            ps.executeUpdate();
        }
    }

    // -----------------------------
    // ATUALIZAR CONTA
    // -----------------------------
    @Override
    public void atualizar(Conta conta) throws SQLException {
        String sql = "UPDATE Conta SET tipo=?, saldo=?, proprietario_cpf=? WHERE numero=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conta instanceof ContaCorrente ? "corrente" : "poupanca");
            ps.setDouble(2, conta.getSaldo());
            ps.setString(3, conta.getProprietario().getCpf());
            ps.setString(4, conta.getNumero());

            ps.executeUpdate();
        }
    }

    // -----------------------------
    // DELETAR CONTA
    // -----------------------------
    @Override
    public void deletar(String numeroConta) throws SQLException {
        Conta conta = buscarPorNumero(numeroConta);
        if (conta == null) return;

        String sql = "DELETE FROM Conta WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroConta);
            ps.executeUpdate();
        }
    }

    // -----------------------------
    // BUSCAR POR NÚMERO
    // -----------------------------
    @Override
    public Conta buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM Conta WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String tipo = rs.getString("tipo");
                    double saldoBD = rs.getDouble("saldo");
                    String cpfProp = rs.getString("proprietario_cpf");
                    double limite = rs.getDouble("limite_cheque_especial");

                    Cliente proprietario = clienteDAO.buscarPorCpf(cpfProp);

                    Conta conta;

                    if ("corrente".equalsIgnoreCase(tipo)) {
                        conta = new ContaCorrente(proprietario, numero, limite);
                    } else {
                        conta = new ContaPoupanca(proprietario, numero);
                    }

                    conta.setSaldoBD(saldoBD);
                    return conta;
                }
            }
        }
        return null;
    }

    // -----------------------------
    // LISTAR POR CLIENTE
    // -----------------------------
    @Override
    public List<Conta> listarPorCliente(String cpfCliente) throws SQLException {
        List<Conta> lista = new ArrayList<>();

        String sql = "SELECT numero FROM Conta WHERE proprietario_cpf = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpfCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(buscarPorNumero(rs.getString("numero")));
            }
        }
        return lista;
    }

    // -----------------------------
    // LISTAR TODAS
    // -----------------------------
    @Override
    public List<Conta> listarTodas() throws SQLException {
        List<Conta> lista = new ArrayList<>();

        String sql = "SELECT numero FROM Conta";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(buscarPorNumero(rs.getString("numero")));
            }
        }
        return lista;
    }

    // -----------------------------
    // ATUALIZAR MAPS EM MEMÓRIA
    // -----------------------------
    public void atualizarMaps(Map<String, Cliente> clientes,
                              Map<String, Conta> contas) {

        contas.clear();

        String sql = "SELECT numero, tipo, saldo, proprietario_cpf FROM Conta";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String numero = rs.getString("numero");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");
                String cpf = rs.getString("proprietario_cpf");
                double limite = rs.getDouble("limite_cheque_especial");

                Cliente cliente = clientes.get(cpf);

                if (cliente == null) {
                    System.out.println("Aviso: Nenhum cliente encontrado para CPF: " + cpf);
                    continue;
                }

                Conta conta;

                if ("corrente".equalsIgnoreCase(tipo)) {
                    conta = new ContaCorrente(cliente, numero,limite);
                } else {
                    conta = new ContaPoupanca(cliente, numero);
                }

                conta.setSaldoBD(saldo);
                contas.put(numero, conta);
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar contas: " + e.getMessage());
        }
    }
}
