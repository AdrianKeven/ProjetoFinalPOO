package operacoes;

import entidades.*;
import utilitarios.ClienteNaoEncontradoException;
import utilitarios.ContaNaoEncontradaException;
import dao.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        clienteDAO.salvar(cliente);   // grava no banco
        clientes.put(cpf, cliente);   // grava no mapa
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);
        clientes.put(cliente.getCpf(), cliente);
    }

    public void removerCliente(String cpf) throws ClienteNaoEncontradoException, SQLException, Exception {

        // 1. Verifica se o cliente existe no mapa
        if (!clientes.containsKey(cpf)) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }

        // 2. Verifica se ele tem contas
        if (contaDAO.temContas(cpf)) {
            throw new Exception("Cliente possui contas e não pode ser removido.");
        }

        // 3. Remove do banco
        clienteDAO.deletar(cpf);

        // 4. Remove do mapa
        clientes.remove(cpf);
    }


    // ---------------------------
    // CRUD CONTAS
    // ---------------------------
    public ContaCorrente abrirContaCorrente(Cliente cliente, double limiteChequeEspecial)
            throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null || !clientes.containsKey(cliente.getCpf()))
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        String tipo = "corrente";
        ContaCorrente conta = new ContaCorrente(cliente, limiteChequeEspecial, tipo);

        contaDAO.salvar(conta);
        contas.put(conta.getNumero(), conta);
        cliente.adicionarConta(conta);

        return conta;
    }

    public ContaPoupanca abrirContaPoupanca(Cliente cliente, String numero)
            throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null || !clientes.containsKey(cliente.getCpf()))
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        String tipo = "poupanca";
        ContaPoupanca conta = new ContaPoupanca(cliente, tipo);

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
        Conta conta = contas.get(numeroConta);

        if (conta == null)
            throw new ContaNaoEncontradaException("Conta não encontrada");

        return conta;
    }

    public Cliente buscarCliente(String cpf) throws ClienteNaoEncontradoException {
        atualizarMapCliente();
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
        return new HashMap<>(clientes);
    }

    public Map<String, Conta> getContas() {
        return new HashMap<>(contas);
    }

    // ---------------------------
    // OPERAÇÕES
    // ---------------------------
    public void realizarDeposito(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).depositar(valor);
    }

    public void realizarSaque(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).sacar(valor);
    }

    public void realizarTransferencia(String origem, String destino, double valor)
            throws ContaNaoEncontradaException {

        Conta cOrigem = buscarConta(origem);
        Conta cDestino = buscarConta(destino);

        cOrigem.transferir(cDestino, valor);
    }

    public void atualizarMapCliente() {
        clienteDAO.atualizarClientesMap(this.clientes);
    }
    public void atualizarMapContas() {
        contaDAO.atualizarMaps(this.clientes,this.contas);
    }
}
