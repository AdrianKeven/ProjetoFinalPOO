package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL_BASE = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL_BANCO = "jdbc:mysql://localhost:3306/bd_contas?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Conexão com o banco bd_contas
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_BANCO, USER, PASSWORD);
    }

    // Inicializa banco e tabelas automaticamente
    public static void inicializar() {
        criarBancoSeNaoExistir();
        criarTabelas();
    }

    // Criar banco se não existir
    private static void criarBancoSeNaoExistir() {
        try (Connection conn = DriverManager.getConnection(URL_BASE, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS bd_contas;");
            System.out.println("Banco bd_contas criado/verificado!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Criar tabelas no banco bd_contas
    private static void criarTabelas() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlCliente = """
                CREATE TABLE IF NOT EXISTS Cliente (
                    cpf VARCHAR(14) NOT NULL PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    endereco VARCHAR(200) NOT NULL
                );
            """;

            String sqlConta = """
                CREATE TABLE IF NOT EXISTS Conta (
                    numero VARCHAR(20) PRIMARY KEY,
                    saldo DECIMAL(10,2) DEFAULT 0,
                    proprietario_cpf VARCHAR(14) NOT NULL,
                    tipo VARCHAR(20) NOT NULL,
                    limite_cheque_especial DECIMAL(10,2) DEFAULT 0,
                    historico TEXT,
                    FOREIGN KEY (proprietario_cpf) REFERENCES Cliente(cpf)
                );
            """;


            stmt.execute(sqlCliente);
            stmt.execute(sqlConta);

            System.out.println("Tabelas criadas/verificadas com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
