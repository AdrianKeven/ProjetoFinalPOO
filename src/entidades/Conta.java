package entidades;

import dao.ContaDAOJdbc;
import utilitarios.SaldoInsuficienteException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public abstract class Conta {

    private final String numero;
    protected double saldo;
    private Cliente proprietario;
    private List<String> historicoTransacoes;
    protected String tipo;

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

    public String getTipo() {
        return tipo;
    }

    public List<String> getHistoricoTransacoes() {
        if (historicoTransacoes == null) {
            historicoTransacoes = new ArrayList<>();
        }
        return historicoTransacoes;
    }


    public void setHistoricoTransacoes(List<String> historico) {
        if (historico == null) {
            this.historicoTransacoes = new ArrayList<>();
        } else {
            this.historicoTransacoes = new ArrayList<>(historico);
        }
    }


    public void setSaldoBD(double saldo) {
        this.saldo = saldo;
    }
    
    public void setProprietario(Cliente novoProprietario) {
        if (novoProprietario == null) {
            throw new IllegalArgumentException("Novo proprietário não pode ser nulo.");
        }

        // se já for o mesmo proprietário, não faz nada
        if (this.proprietario != null && this.proprietario.equals(novoProprietario)) {
            return;
        }

        // remove a conta da lista do proprietário atual (se existir)
        if (this.proprietario != null) {
            try {
                this.proprietario.getContas().remove(this);
            } catch (Exception ignored) {}
        }

        // define o novo proprietário e adiciona a conta à lista dele
        this.proprietario = novoProprietario;
        try {
            novoProprietario.adicionarConta(this);
        } catch (Exception ignored) {}
    }

    public void adicionarTransacao(String descricao) {
        if (historicoTransacoes == null) {
            historicoTransacoes = new ArrayList<>();
        }

        String registro = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        ) + " - " + descricao;

        historicoTransacoes.add(registro);
    }


    public abstract void depositar(double valor);

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract void transferir(Conta destino, double valor) throws SaldoInsuficienteException;

    public double getLimiteChequeEspecial(){return 0.0;}

    @Override
    public String toString() {
        return "Conta nº " + numero +
                " | Titular: " + proprietario.getNome() +
                " | Saldo: R$ " + String.format("%.2f", saldo);
    }
}
