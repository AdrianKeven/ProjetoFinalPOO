package entidades;

import utilitarios.SaldoInsuficienteException;

public class ContaPoupanca extends Conta {

    private final double TAXA_RENDIMENTO_MENSAL = 0.01;

    public ContaPoupanca(Cliente proprietario, String numero) {
        super(proprietario,numero);
    }

    @Override
    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
            adicionarTransacao(String.format("Depósito: R$ %.2f", valor));
        } else {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que 0");
        }
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser maior que 0");
        }

        if (valor <= this.saldo) {
            this.saldo -= valor;
            adicionarTransacao(String.format("Saque: R$ %.2f", valor));
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para efetuar saque");
        }
    }

    @Override
    public void tranferir(Conta destino, double valor) throws SaldoInsuficienteException {
        this.sacar(valor);
        destino.depositar(valor);
        adicionarTransacao(String.format(
                "Transferência de R$ %.2f para a conta nº %s (Titular: %s)",
                valor,
                destino.getNumero(),
                destino.getProprietario().getNome()
        ));
    }

    public void renderJuros() {
        this.saldo *= (1 + this.TAXA_RENDIMENTO_MENSAL);
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(" | Taxa Rendimento Mensal: %.2f%%", this.TAXA_RENDIMENTO_MENSAL * 100);
    }
}
