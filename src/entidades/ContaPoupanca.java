package entidades;

import utilitarios.SaldoInsuficienteException;

public class ContaPoupanca extends Conta {

    private static final double TAXA_RENDIMENTO_MENSAL = 0.01;

    public ContaPoupanca(Cliente proprietario, String numero) {
        super(proprietario, numero);
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0)
            throw new IllegalArgumentException("Valor de depósito deve ser maior que 0.");

        this.saldo += valor;
        adicionarTransacao(String.format("Depósito: R$ %.2f", valor));
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0)
            throw new IllegalArgumentException("Valor de saque deve ser maior que 0.");

        if (valor > saldo)
            throw new SaldoInsuficienteException("Saldo insuficiente para saque.");

        saldo -= valor;
        adicionarTransacao(String.format("Saque: R$ %.2f", valor));
    }

    @Override
    public void transferir(Conta destino, double valor) throws SaldoInsuficienteException {
        this.sacar(valor);
        destino.depositar(valor);

        adicionarTransacao(String.format(
                "Transferência de R$ %.2f para conta %s (Titular: %s)",
                valor,
                destino.getNumero(),
                destino.getProprietario().getNome()
        ));
    }

    /**
     * Aplica rendimento mensal de 1%.
     */
    public void renderJuros() {
        double juros = saldo * TAXA_RENDIMENTO_MENSAL;
        saldo += juros;

        adicionarTransacao(String.format(
                "Rendimento aplicado: R$ %.2f (taxa %.2f%%)",
                juros, TAXA_RENDIMENTO_MENSAL * 100
        ));
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(" | Rendimento Mensal: %.2f%%", TAXA_RENDIMENTO_MENSAL * 100);
    }
}
