package operacoes;

import entidades.*;
import utilitarios.ClienteNaoEncontradoException;
import utilitarios.ContaNaoEncontradaException;
import dao.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utilitarios.ValidadorCliente.gerarNumeroConta;

public class BancoService {

    private final String nomeBanco;
     private final Map<String, Cliente> clientes;
    private final Map<String, Conta> contas;

    private final ContaDAOJdbc contaDAO;
    private final ClienteDAOJdbc clienteDAO;

    public BancoService(String nomeBanco) {
        this.nomeBanco = nomeBanco;
        this.clientes = new HashMap<>();
        this.contas = new HashMap<>();
        this.clienteDAO = new ClienteDAOJdbc();
        this.contaDAO = new ContaDAOJdbc();
    }

    // ---------------------------
    // CRUD CLIENTE
    // ---------------------------
    public void cadastrarCliente(String nome, String cpf, String endereco) throws SQLException {

        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome inválido");

        if (cpf == null || cpf.isBlank())
            throw new IllegalArgumentException("CPF inválido");

        if (endereco == null || endereco.isBlank())
            throw new IllegalArgumentException("Endereço inválido");

        if (clientes.containsKey(cpf))
            throw new IllegalArgumentException("CPF já cadastrado no sistema");

        Cliente cliente = new Cliente(nome, cpf, endereco);

        clienteDAO.salvar(cliente);
        clientes.put(cpf, cliente);
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);
        clientes.put(cliente.getCpf(), cliente);
    }

    public void removerCliente(String cpf) throws ClienteNaoEncontradoException, SQLException, Exception {

        if (!clientes.containsKey(cpf)) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }

        if (contaDAO.temContas(cpf)) {
            throw new Exception("Cliente possui contas e não pode ser removido.");
        }

        clienteDAO.deletar(cpf);

        clientes.remove(cpf);
    }


    // ---------------------------
    // CRUD CONTAS
    // ---------------------------
    public ContaCorrente abrirContaCorrente(Cliente cliente, double limiteChequeEspecial)
            throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null || !clientes.containsKey(cliente.getCpf()))
            throw new ClienteNaoEncontradoException("Cliente não encontrado");

        String numero = gerarNumeroConta();

        ContaCorrente conta = new ContaCorrente(numero, cliente, limiteChequeEspecial);

        contaDAO.salvar(conta);
        contas.put(numero, conta);
        cliente.adicionarConta(conta);

        return conta;
    }

    public ContaPoupanca abrirContaPoupanca(Cliente cliente)
            throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null || !clientes.containsKey(cliente.getCpf()))
            throw new ClienteNaoEncontradoException("Cliente não encontrado");

        String numero = gerarNumeroConta();

        ContaPoupanca conta = new ContaPoupanca(numero, cliente);

        contaDAO.salvar(conta);
        contas.put(numero, conta);
        cliente.adicionarConta(conta);

        return conta;
    }


    public void atualizarContas(Conta conta) throws SQLException {
        contaDAO.atualizar(conta);
        contas.put(conta.getNumero(), conta);
    }

    public void removerConta(String numeroConta) throws ContaNaoEncontradaException, SQLException {

        if (!contas.containsKey(numeroConta))
            throw new ContaNaoEncontradaException("Conta não encontrada");

        Conta conta = contas.get(numeroConta);

        Cliente cli = conta.getProprietario();
        if (cli != null)
            cli.getContas().remove(conta);

        contaDAO.deletar(numeroConta);
        contas.remove(numeroConta);
    }

    // ---------------------------
    // BUSCAS
    // ---------------------------
    public Conta buscarConta(String numeroConta) throws ContaNaoEncontradaException {
        atualizarMapContas();

        //Log Atualizacao Map
        System.out.println("Map atualizado");

        Conta conta = contas.get(numeroConta);

        if (conta == null)
            throw new ContaNaoEncontradaException("Conta não encontrada");

        return conta;
    }

    public Cliente buscarCliente(String cpf) throws ClienteNaoEncontradoException {
        atualizarMapCliente();

        //Log Atualizacao Map
        System.out.println("Map atualizado");

        Cliente c = clientes.get(cpf);

        if (c == null)
            throw new ClienteNaoEncontradoException("Cliente não encontrado");

        return c;
    }


    public List<Conta> buscarContaProprietario(String cpf) throws ContaNaoEncontradaException, SQLException {
        return contaDAO.listarPorCliente(cpf);
    }

    // ---------------------------
    // LISTAGENS
    // ---------------------------
    public List<Conta> listarTodasContaClinte(String cpf) throws ClienteNaoEncontradoException, SQLException {

        if (!clientes.containsKey(cpf))
            throw new ClienteNaoEncontradoException("Cliente não encontrado");

        return contaDAO.listarPorCliente(cpf);
    }

    public List<Conta> listarTodasContas() throws SQLException {
        return contaDAO.listarTodas();
    }

    public List<Cliente> listarTodosClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    // ---------------------------
    // GETTERS
    // ---------------------------
    public String getNomeBanco() {
        return nomeBanco;
    }

    public Map<String, Cliente> getClientes() {
        atualizarMapCliente();

        //Log Atualizacao Map
        System.out.println("Map atualizado");

        return this.clientes;
    }

    public Map<String, Conta> getContas() {
        atualizarMapContas();

        //Log Atualizacao Map
        System.out.println("Map atualizado");

        return this.contas;
    }

    // ---------------------------
    // OPERAÇÕES
    // ---------------------------
    public void realizarDeposito(String numeroConta, double valor) throws ContaNaoEncontradaException, SQLException {
        buscarConta(numeroConta).depositar(valor);

        double novoSaldo = contaDAO.buscarSaldoBD(numeroConta) + valor;
        contaDAO.atualizarSaldoBD(numeroConta,novoSaldo);

        //Log de Deposito
        System.out.println("Operacao de Deposito realizada");
        System.out.println("Saldo da conta: " + contaDAO.buscarSaldoBD(numeroConta));
    }

    public void realizarSaque(String numeroConta, double valor) throws ContaNaoEncontradaException, SQLException {
        buscarConta(numeroConta).sacar(valor);

        double novoSaldo = contaDAO.buscarSaldoBD(numeroConta) - valor;
        contaDAO.atualizarSaldoBD(numeroConta,novoSaldo);

        //log de Saque
        System.out.println("Operacao de Saque realizada");
        System.out.println("Saldo da conta: " + contaDAO.buscarSaldoBD(numeroConta));
    }

    public void realizarTransferencia(String origem, String destino, double valor)
            throws ContaNaoEncontradaException, SQLException {

        Conta cOrigem = buscarConta(origem);
        Conta cDestino = buscarConta(destino);

        cOrigem.transferir(cDestino, valor);
        double novoSaldoOrigem = contaDAO.buscarSaldoBD(origem) - valor;
        double novoSaldoDestino = contaDAO.buscarSaldoBD(destino) + valor;

        contaDAO.atualizarSaldoBD(origem,novoSaldoOrigem);
        contaDAO.atualizarSaldoBD(destino,novoSaldoDestino);

        // log de Transferencia
        System.out.println("Transferencia realizada");
        System.out.println("Saldo da origem: " + contaDAO.buscarSaldoBD(origem));
        System.out.println("Saldo do destino: " + contaDAO.buscarSaldoBD(destino));
    }

    public void atualizarMapCliente() {
        clienteDAO.atualizarClientesMap(this.clientes);
    }
    public void atualizarMapContas() {
        contaDAO.atualizarMaps(this.clientes,this.contas);
    }
}
