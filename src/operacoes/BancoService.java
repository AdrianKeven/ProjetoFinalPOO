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

    //CRUD DE CLIENTE
    public void cadastrarCliente(String nome, String cpf, String endereco) throws SQLException {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF inválido");
        }
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço inválido");
        }

        if (clientes.containsKey(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado no banco de clientes");
        }

        // Cria o cliente corretamente
        Cliente cliente = new Cliente(nome, cpf, endereco);

        // Grava no banco primeiro
        clienteDAO.salvar(cliente);

        // Só coloca no mapa se o banco salvar com sucesso
        clientes.put(cpf, cliente);
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);
    }

    public void removerCliente(String cpf) throws ClienteNaoEncontradoException, SQLException {
        if (!clientes.containsKey(cpf)) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        clientes.remove(cpf);
        clienteDAO.deletar(cpf);
    }

    //CRUD DE CONTAS
    public ContaCorrente abrirContaCorrente(Cliente cliente,String numero, double limiteChequeEspecial) throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        if (!clientes.containsKey(cliente.getCpf())) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }
                
        ContaCorrente novaConta = new ContaCorrente(cliente,numero,limiteChequeEspecial);
        contaDAO.salvar(novaConta);
        contas.put(novaConta.getNumero(),novaConta);
        cliente.adicionarConta(novaConta);
        return novaConta;
    }

    public ContaPoupanca abrirContaPoupanca(Cliente cliente, String numero) throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        if (!clientes.containsKey(cliente.getCpf())) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        ContaPoupanca novaConta = new ContaPoupanca(cliente,numero);
        contaDAO.salvar(novaConta);
        contas.put(numero,novaConta);
        cliente.adicionarConta(novaConta);
        return novaConta;
    }

    public void atualizarContas(Conta conta) throws SQLException {
        contaDAO.atualizar(conta);
    }

    public void removerConta(String numeroConta) throws ContaNaoEncontradaException, SQLException {

        if (!contas.containsKey(numeroConta)) {
            throw new ContaNaoEncontradaException("Conta não encontrada");
        }

        Conta conta = contas.get(numeroConta);

        Cliente proprietario = conta.getProprietario();
        if (proprietario != null) {
            proprietario.getContas().remove(conta);
        }
        contas.remove(numeroConta);
        contaDAO.deletar(numeroConta);
    }

    //METODOS DE BUSCA
    public Conta buscarConta(String numeroConta) throws ContaNaoEncontradaException {
        return contas.get(numeroConta);
    }
    
    public Cliente buscarCliente(String cpf) {
    if (clientes.containsKey(cpf)) {
        return clientes.get(cpf);
    }

    throw new RuntimeException("Cliente não encontrado");
    }

    //METODOS DE LISTAGEM
    public List<Conta> listarTodasContaClinte(String cpf) throws ClienteNaoEncontradoException,SQLException {
        if (!clientes.containsKey(cpf)) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        return contaDAO.listarPorCliente(cpf);
    }

    public List<Conta> listarTodasContas() throws SQLException {
        return contaDAO.listarTodas();
    }

    public List<Cliente> listarTodosClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    //GETERS
    public String getNomeBanco() {
        return nomeBanco;
    }

    public Map<String, Cliente> getClientes() {
        return new HashMap<>(this.clientes);
    }

    public Map<String, Conta> getContas() {
        return new HashMap<>(this.contas);
    }

    //METODOS DA LOGICA DE NEGOCIO
    public void realizarDeposito(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).depositar(valor);
    }

    public void realizarSaque(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).sacar(valor);
    }

    public void realizarTransferencia(String contaOrigem, String contaDestino,double valor) throws ContaNaoEncontradaException {
        buscarConta(contaOrigem).tranferir(buscarConta(contaDestino),valor);
    }
}
