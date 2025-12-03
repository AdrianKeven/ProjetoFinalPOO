package entidades;

import dao.ContaDAOJdbc;
import utilitarios.SaldoInsuficienteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public abstract class Conta {

    private final String numero;
    protected double saldo;
    private final Cliente proprietario;
    private final List<String> historicoTransacoes;
    protected String tipo;

    public Conta(Cliente proprietario, String tipo) {

        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietário não pode ser nulo.");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int numero = random.nextInt(10); // gera número de 0 a 9
            sb.append(numero);
        }
        String numero = sb.toString();

        this.proprietario = proprietario;
        this.numero = numero.trim();
        this.saldo = 0;
        this.historicoTransacoes = new ArrayList<>();
        this.tipo = tipo.toLowerCase(Locale.ROOT).trim();

        // ADICIONA AUTOMATICAMENTE A CONTA AO CLIENTE
        proprietario.adicionarConta(this);
    }

    public Conta(String numero, Cliente proprietario, double saldo, String tipo) {
        if (proprietario == null)
            throw new IllegalArgumentException("Proprietário não pode ser nulo.");

        this.numero = numero;
        this.proprietario = proprietario;
        this.saldo = saldo;
        this.tipo = tipo.toLowerCase(Locale.ROOT).trim();
        this.historicoTransacoes = new ArrayList<>();

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
