package entidades;

import utilitarios.SaldoInsuficienteException;
import java.util.ArrayList;
import java.util.List;

public abstract class Conta {

    private String numero;
    protected double saldo;
    private Cliente proprietario;
    private List<String> historicoTransacoes;

    public Conta(Cliente proprietario,String numero) {
        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietario não pode ser nulo.");
        }

        this.proprietario = proprietario;
        this.saldo = 0;
        this.historicoTransacoes = new ArrayList<>();
        this.numero = numero;

    }

    public String  getNumero() {
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

    public abstract void tranferir(Conta destino, double valor) throws SaldoInsuficienteException;

    @Override
    public String toString() {
        return "Conta nº " + numero +
                " | Titular: " + proprietario.getNome() +
                " | Saldo: R$ " + String.format("%.2f", saldo);
    }
}
