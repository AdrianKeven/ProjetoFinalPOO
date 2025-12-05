import dao.ContaDAOJdbc;
import entidades.Cliente;
import entidades.Conta;
import entidades.ContaCorrente;
import operacoes.BancoService;
import util.DBConnection;

public class Main {

    public static void main(String[] args) {

        try {
            DBConnection.inicializar();

            ContaDAOJdbc contaDAO = new ContaDAOJdbc();
            BancoService service = new BancoService("delegas");

            //---------------------------------------
            // CADASTRAR CLIENTES (COM CPFs CORRETOS)
            //---------------------------------------
            service.cadastrarCliente("Adrian", "15155096642", "Rua A, 123");
            service.cadastrarCliente("Maria Oliveira", "75596899062", "Rua B, 456");

            //---------------------------------------
            // CRIAR E SALVAR CONTAS
            //---------------------------------------
            ContaCorrente conta1 = new ContaCorrente("1001",
                    new Cliente("Adrian", "15155096642", "Rua A, 123"),
                    500);

            ContaCorrente conta2 = new ContaCorrente("2002",
                    new Cliente("Maria Oliveira", "75596899062", "Rua B, 456"),
                    300);

            contaDAO.salvar(conta1);
            contaDAO.salvar(conta2);

            System.out.println("Clientes e contas cadastrados!\n");

            //---------------------------------------
            // TESTE DEPÓSITO
            //---------------------------------------
            System.out.println("\n=== TESTE DEPÓSITO ===");
            service.realizarDeposito("1001", 200.0);

            double saldo1 = contaDAO.buscarSaldoBD("1001");
            System.out.println("Saldo após depósito (esperado 200.00): " + saldo1);

            //---------------------------------------
            // TESTE SAQUE
            //---------------------------------------
            System.out.println("\n=== TESTE SAQUE ===");
            service.realizarSaque("1001", 50.0);

            double saldo1DepoisSaque = contaDAO.buscarSaldoBD("1001");
            System.out.println("Saldo após saque (esperado 150.00): " + saldo1DepoisSaque);

            //---------------------------------------
            // TESTE TRANSFERÊNCIA
            //---------------------------------------
            System.out.println("\n=== TESTE TRANSFERÊNCIA ===");
            service.realizarTransferencia("1001", "2002", 30.0);

            double saldoOrigem = contaDAO.buscarSaldoBD("1001");
            double saldoDestino = contaDAO.buscarSaldoBD("2002");

            System.out.println("Saldo origem após transferência (esperado 120.00): " + saldoOrigem);
            System.out.println("Saldo destino após transferência (esperado 330.00 => já tinha 300 + 30): " + saldoDestino);

            //---------------------------------------
            // VER HISTÓRICO
            //---------------------------------------
            System.out.println("\n=== HISTÓRICO DA CONTA 1001 ===");
            Conta contaTestada = contaDAO.buscarPorNumero("1001");
            contaTestada.getHistoricoTransacoes().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
