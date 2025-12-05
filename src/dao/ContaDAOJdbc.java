package dao;

import entidades.*;
import util.DBConnection;
import utilitarios.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContaDAOJdbc implements ContaDAO {

    private final ClienteDAO clienteDAO = new ClienteDAOJdbc();

    // ----------------------------------------------------
    // SALVAR CONTA
    // ----------------------------------------------------
    @Override
    public void salvar(Conta conta) throws SQLException {

        String sql = "INSERT INTO Conta (numero, saldo, proprietario_cpf, tipo, limite_cheque_especial, historico) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conta.getNumero());
            ps.setDouble(2, conta.getSaldo());
            ps.setString(3, conta.getProprietario().getCpf());
            ps.setString(4, conta instanceof ContaCorrente ? "corrente" : "poupanca");

            // limite
            if (conta instanceof ContaCorrente cc) {
                ps.setDouble(5, cc.getLimiteChequeEspecial());
            } else {
                ps.setDouble(5, 0.0);
            }

            // histórico → string
            ps.setString(6, Util.historicoToString(conta.getHistoricoTransacoes()));

            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------
    // ATUALIZAR CONTA
    // ----------------------------------------------------
    @Override
    public void atualizar(Conta conta) throws SQLException {

        String sql = "UPDATE Conta SET tipo=?, saldo=?, proprietario_cpf=?, limite_cheque_especial=?, historico=? "
                + "WHERE numero=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conta instanceof ContaCorrente ? "corrente" : "poupanca");
            ps.setDouble(2, conta.getSaldo());
            ps.setString(3, conta.getProprietario().getCpf());

            if (conta instanceof ContaCorrente cc) {
                ps.setDouble(4, cc.getLimiteChequeEspecial());
            } else {
                ps.setDouble(4, 0.0);
            }

            // histórico → string
            ps.setString(5, Util.historicoToString(conta.getHistoricoTransacoes()));

            ps.setString(6, conta.getNumero());

            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------
    // ATUALIZAR SOMENTE SALDO
    // ----------------------------------------------------
    public void atualizarSaldoBD(String numeroConta, double novoSaldo) throws SQLException {
        String sql = "UPDATE Conta SET saldo = ? WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, novoSaldo);
            ps.setString(2, numeroConta);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Conta não encontrada: " + numeroConta);
            }
        }
    }

    // ----------------------------------------------------
    // DELETAR CONTA
    // ----------------------------------------------------
    @Override
    public void deletar(String numeroConta) throws SQLException {

        String sql = "DELETE FROM Conta WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroConta);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------
    // BUSCAR POR NÚMERO
    // ----------------------------------------------------
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
                    String historicoTexto = rs.getString("historico");

                    Cliente proprietario = clienteDAO.buscarPorCpf(cpfProp);

                    Conta conta;

                    if ("corrente".equalsIgnoreCase(tipo)) {
                        conta = new ContaCorrente(numero, proprietario, limite);
                    } else {
                        conta = new ContaPoupanca(numero, proprietario);
                    }

                    // saldo do BD
                    conta.setSaldoBD(saldoBD);

                    // histórico
                    conta.setHistoricoTransacoes(Util.stringToHistorico(historicoTexto));

                    return conta;
                }
            }
        }

        return null;
    }

    // ----------------------------------------------------
    // BUSCAR SALDO
    // ----------------------------------------------------
    public double buscarSaldoBD(String numeroConta) throws SQLException {

        String sql = "SELECT saldo FROM Conta WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroConta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("saldo");
            }

            throw new SQLException("Conta não encontrada: " + numeroConta);
        }
    }

    // ----------------------------------------------------
    // LISTAR POR CLIENTE
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // LISTAR TODAS AS CONTAS
    // ----------------------------------------------------
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

    // ----------------------------------------------------
    // ATUALIZAR MAPS EM MEMÓRIA
    // ----------------------------------------------------
    public void atualizarMaps(Map<String, Cliente> clientes,
                              Map<String, Conta> contas) {

        contas.clear();

        String sql = "SELECT * FROM Conta";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String numero = rs.getString("numero");
                double saldo = rs.getDouble("saldo");
                String tipo = rs.getString("tipo");
                String cpf = rs.getString("proprietario_cpf");
                double limite = rs.getDouble("limite_cheque_especial");
                String historicoTexto = rs.getString("historico");

                Cliente cliente = clientes.get(cpf);
                if (cliente == null) continue;

                Conta conta;

                if ("corrente".equalsIgnoreCase(tipo)) {
                    conta = new ContaCorrente(numero, cliente, limite);
                } else {
                    conta = new ContaPoupanca(numero, cliente);
                }

                conta.setSaldoBD(saldo);
                conta.setHistoricoTransacoes(Util.stringToHistorico(historicoTexto));

                contas.put(numero, conta);
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar contas: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // TEM CONTAS DO CLIENTE?
    // ----------------------------------------------------
    public boolean temContas(String cpf) throws SQLException {

        String sql = "SELECT COUNT(*) FROM Conta WHERE proprietario_cpf = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        }
    }
}
