package projetofinal.operacoes;

import projetofinal.entidades.Cliente;
import projetofinal.entidades.Conta;
import projetofinal.entidades.ContaCorrente;
import projetofinal.entidades.ContaPoupanca;
import projetofinal.utilitarios.ClienteNaoEncontradoException;
import projetofinal.utilitarios.ContaNaoEncontradaException;
import projetofinal.utilitarios.SaldoInsuficienteException;

import java.util.HashMap;
import java.util.Map;

public class BancoService {
    private String nomeBanco;
    private Map<String, Cliente> clientes;
    private Map<String, Conta> contas;
    private int proximoNumeroConta = 1;

    public BancoService(String nomeBanco) {
        this.nomeBanco = nomeBanco;
        this.clientes = new HashMap<>();
        this.contas = new HashMap<>();
    }

    public void cadastrarCliente(Cliente cliente){
        if (clientes.containsKey(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF ja presente no Banco de clientes");
        } else {
            clientes.put(cliente.getCpf(),cliente);
        }
    }

    public Conta abrirConta(Cliente cliente, String tipoConta, double limiteChequeEspecial)throws ClienteNaoEncontradoException {

        if (cliente == null) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        if (!clientes.containsKey(cliente.getCpf())) {
            throw new ClienteNaoEncontradoException("Cliente nao encontrado");
        }

        tipoConta = tipoConta.trim().toLowerCase();

        if (tipoConta.equals("corrente")) {
            ContaCorrente retorno = new ContaCorrente(String.valueOf(this.proximoNumeroConta),cliente,limiteChequeEspecial);
            this.proximoNumeroConta++;
            contas.put(retorno.getNumero(),retorno);
            return retorno;
        } else if (tipoConta.equals("poupanca")){
            ContaPoupanca retorno = new ContaPoupanca(String.valueOf(this.proximoNumeroConta),cliente);
            this.proximoNumeroConta++;
            contas.put(retorno.getNumero(),retorno);
            return retorno;
        } else {
            throw new IllegalArgumentException("Tipo da conta nao encontrado (corrente ou poupanca)");
        }
    }

    public Conta buscarConta(String numeroConta) throws ContaNaoEncontradaException {
        for (Conta conta : contas.values()) {
            if (conta.getNumero().equals(numeroConta)) {
                return conta;
            }
        }
        throw new ContaNaoEncontradaException("Conta nao encontrada");
    }

    public void realizarDeposito(String numeroConta, double valor) throws ContaNaoEncontradaException {
        buscarConta(numeroConta).depositar(valor);
    }

    public void realizarSaque(String numeroConta, double valor) throws ContaNaoEncontradaException, SaldoInsuficienteException {
        buscarConta(numeroConta).sacar(valor);
    }

    public void realizarTransferencia(String contaOrigem, String contaDestino,double valor) throws ContaNaoEncontradaException, SaldoInsuficienteException {
        buscarConta(contaOrigem).tranferir(buscarConta(contaDestino),valor);
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
