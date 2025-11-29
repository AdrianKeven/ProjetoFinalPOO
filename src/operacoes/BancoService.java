package operacoes;

import entidades.*;
import utilitarios.ClienteNaoEncontradoException;
import utilitarios.ContaNaoEncontradaException;
import dao.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BancoService {
    private String nomeBanco;
    private Map<String, Cliente> clientes;
    private Map<String, Conta> contas;
    private int proximoNumeroConta = 1;
    private ContaDAOJdbc contaDAO;
    private ClienteDAOJdbc clienteDAO;


    public BancoService(String nomeBanco) {
        this.nomeBanco = nomeBanco;
        this.clientes = new HashMap<>();
        this.contas = new HashMap<>();
        this.clienteDAO = new ClienteDAOJdbc();
        this.contaDAO = new ContaDAOJdbc();
    }

    public void cadastrarCliente(Cliente cliente) throws SQLException {
        if (clientes.containsKey(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF ja presente no Banco de clientes");
        } else {
            clienteDAO.salvar(cliente);
        }
    }

    public ContaCorrente abrirContaCorrente(Cliente cliente,String numero, double limiteChequeEspecial) throws ClienteNaoEncontradoException, SQLException {

        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        if (!clientes.containsKey(cliente.getCpf())) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }
                
        ContaCorrente novaConta = new ContaCorrente(cliente,numero,limiteChequeEspecial);
        contaDAO.salvar(novaConta);
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
        return novaConta;
    }


    public void removerConta(String numeroConta) throws ContaNaoEncontradaException, SQLException {

    if (!contas.containsKey(numeroConta)) {
        throw new ContaNaoEncontradaException("Conta não encontrada");
    }
    contas.remove(numeroConta);
    contaDAO.deletar(numeroConta);
}

    public Conta buscarConta(String numeroConta) throws ContaNaoEncontradaException {
        return contas.get(numeroConta);
    }
    
    public Cliente buscarCliente(String cpf) {
    if (clientes.containsKey(cpf)) {
        return clientes.get(cpf);
    }
    throw new RuntimeException("Cliente não encontrado");
}

    public void realizarDeposito(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).depositar(valor);
    }

    public void realizarSaque(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).sacar(valor);
    }

    public void realizarTransferencia(String contaOrigem, String contaDestino,double valor) throws ContaNaoEncontradaException {
        buscarConta(contaOrigem).tranferir(buscarConta(contaDestino),valor);
    }
    
    public void removerCliente(String cpf) throws ClienteNaoEncontradoException {
    if (!clientes.containsKey(cpf)) {
        throw new ClienteNaoEncontradoException("Cliente não encontrado");
    }

    clientes.remove(cpf);
}

    public String getNomeBanco() {
        return nomeBanco;
    }

    public Map<String, Cliente> getClientes() {
        return new HashMap<>(this.clientes);
    }

    public Map<String, Conta> getContas() {
        return new HashMap<>(this.contas);
    }
}
