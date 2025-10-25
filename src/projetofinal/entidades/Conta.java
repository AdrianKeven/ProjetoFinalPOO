package projetofinal.entidades;

import projetofinal.utilitarios.SaldoInsuficienteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Conta {
    private String numero;
    protected double saldo;
    private Cliente proprietario;
    private List<String> historicoTransacoes;

    public Conta(String numero, Cliente proprietario) {
        //Pode checar no Banco se existe um numero repetido
        if (numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException("Numero da conta invalido.");
        }
        if (proprietario == null) {
            throw new IllegalArgumentException("Proprietario não pode ser nulo.");
        }
        this.numero = numero;
        this.proprietario = proprietario;
        this.saldo = 0;
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

    public abstract void depositar(double valor);

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract void tranferir(Conta destino,double valor) throws  SaldoInsuficienteException;

    @Override
    public boolean equals(Object obj2) {
        //this referencia a objeto 1 e o Obj2 o objeto 2
        // verifica se sao o mesmo obj
        if (this == obj2) {
            return true;
        }
        //verifica se e nulo ou classes diferentes
        if (obj2 == null || getClass() != obj2.getClass()) {
            return false;
        }
        //compara o CPF
        Conta c2 = (Conta) obj2;
        return Objects.equals(numero, c2.numero);
    }

    @Override
    public String toString() {
        return "Conta nº " + numero +
                " | Titular: " + proprietario.getNome() +
                " | Saldo: R$ " + String.format("%.2f", saldo);
    }
}
