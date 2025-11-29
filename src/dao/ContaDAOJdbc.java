
package dao;

import entidades.*;
import util.DBConnection;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAOJdbc implements ContaDAO {

    private final ClienteDAO clienteDAO = new ClienteDAOJdbc();

    @Override
    public void salvar(Conta conta) throws SQLException {
        String sql = "INSERT INTO contas (numero, tipo, saldo, limite_cheque_especial, proprietario_cpf) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conta.getNumero());

            if (conta instanceof ContaCorrente cc) {
                ps.setString(2, "corrente");
                ps.setDouble(3, cc.getSaldo());
                ps.setDouble(4, cc.getLimiteChequeEspecial());
            } else if (conta instanceof ContaPoupanca) {
                ps.setString(2, "poupanca");
                ps.setDouble(3, conta.getSaldo());
                ps.setNull(4, Types.DOUBLE);
            }

            ps.setString(5, conta.getProprietario().getCpf());
            ps.executeUpdate();
        }
    }

    @Override
    public void atualizar(Conta conta) throws SQLException {
        String sql = "UPDATE contas SET tipo = ?, saldo = ?, limite_cheque_especial = ?, proprietario_cpf = ? WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conta instanceof ContaCorrente cc) {
                ps.setString(1, "corrente");
                ps.setDouble(2, cc.getSaldo());
                ps.setDouble(3, cc.getLimiteChequeEspecial());
            } else if (conta instanceof ContaPoupanca) {
                ps.setString(1, "poupanca");
                ps.setDouble(2, conta.getSaldo());
                ps.setNull(3, Types.DOUBLE);
            }

            ps.setString(4, conta.getProprietario().getCpf());
            ps.setString(5, conta.getNumero());

            ps.executeUpdate();
        }
    }
    public int contarContasPorCliente(String cpf) throws SQLException {
    String sql = "SELECT COUNT(*) FROM contas WHERE proprietario_cpf = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cpf);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) return rs.getInt(1);
    }
    return 0;
}

@Override
public void deletar(String numeroConta) throws SQLException {

    Conta conta = buscarPorNumero(numeroConta);
    if (conta == null) return;

    String cpf = conta.getProprietario().getCpf();
  
    String sql = "DELETE FROM contas WHERE numero = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, numeroConta);
        ps.executeUpdate();
    }

    if (contarContasPorCliente(cpf) == 0) {
        ClienteDAO clienteDAO = new ClienteDAOJdbc();
        clienteDAO.deletar(cpf);
        System.out.println("Cliente removido automaticamente (nao possui mais contas).");
    }
}

public boolean deletarRetornaSucesso(String numeroConta) throws SQLException {
    String sql = "DELETE FROM contas WHERE numero = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, numeroConta);
        int rows = ps.executeUpdate();
        return rows > 0;
    }
}

    @Override
    public Conta buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM contas WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String tipo = rs.getString("tipo");
                    double saldoBD = rs.getDouble("saldo");
                    String cpfProp = rs.getString("proprietario_cpf");

                    Cliente proprietario = clienteDAO.buscarPorCpf(cpfProp);

                    Conta conta;

                    if (tipo.equals("corrente")) {
                        conta = new ContaCorrente(
                                rs.getString("numero"),
                                proprietario,
                                rs.getDouble("limite_cheque_especial")
                        );
                    } else {
                        conta = new ContaPoupanca(
                                rs.getString("numero"),
                                proprietario
                        );
                    }

                    try {
                        Field f = Conta.class.getDeclaredField("saldo");
                        f.setAccessible(true);
                        f.setDouble(conta, saldoBD);
                    } catch (Exception ignored) {}

                    return conta;
                }
            }
        }

        return null;
    }

    @Override
    public List<Conta> listarPorCliente(String cpfCliente) throws SQLException {
        List<Conta> lista = new ArrayList<>();

        String sql = "SELECT numero FROM contas WHERE proprietario_cpf = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpfCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conta c = buscarPorNumero(rs.getString("numero"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    @Override
    public List<Conta> listarTodas() throws SQLException {
        List<Conta> lista = new ArrayList<>();

        String sql = "SELECT numero FROM contas";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Conta c = buscarPorNumero(rs.getString("numero"));
                lista.add(c);
            }
        }
        return lista;
    }
}

