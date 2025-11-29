package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/bd_contas?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // altere se necessário
    private static final String PASSWORD = ""; // altere se necessário

    static {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) { 
            System.out.println("Conexao estabelecida com sucesso!"); 
        } catch (Exception e) { 
            System.err.println("Erro ao conectar: " + e.getMessage()); 
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
