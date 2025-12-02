package entidades;

import utilitarios.SaldoInsuficienteException;
import java.util.ArrayList;
import java.util.List;

public abstract class Conta {

    private final String numero;
    protected double saldo;
    private final Cliente proprietario;
    private final List<String> historicoTransacoes;

    public Conta(Cliente proprietario, String numero) {

        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietário não pode ser nulo.");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número da conta inválido.");
        }

        this.proprietario = proprietario;
        this.numero = numero.trim();
        this.saldo = 0;
        this.historicoTransacoes = new ArrayList<>();

        // ADICIONA AUTOMATICAMENTE A CONTA AO CLIENTE
        proprietario.adicionarConta(this);
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getProprietario() {
        return proprietario;
    }

    public List<String> getHistoricoTransacoes() {
        return historicoTransacoes;
    }

    protected void adicionarTransacao(String descricao) {
        historicoTransacoes.add(descricao);
    }

    public void setSaldoBD(double saldo) {
        this.saldo = saldo;
    }

    public abstract void depositar(double valor);

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract void transferir(Conta destino, double valor) throws SaldoInsuficienteException;

    @Override
    public String toString() {
        return "Conta nº " + numero +
                " | Titular: " + proprietario.getNome() +
                " | Saldo: R$ " + String.format("%.2f", saldo);
    }
}
