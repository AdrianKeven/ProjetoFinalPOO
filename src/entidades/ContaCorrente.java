package entidades;

import utilitarios.SaldoInsuficienteException;

public class ContaCorrente extends Conta {

    private double limiteChequeEspecial;

    public ContaCorrente(String numero, Cliente proprietario, double limiteChequeEspecial) {
        super(numero, proprietario, 0.0, "corrente");
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    // Necessário para quando carregar do banco via DAO
    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
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

        // Se o saldo é suficiente → saque normal
        if (valor <= saldo) {
            saldo -= valor;
            adicionarTransacao(String.format("Saque: R$ %.2f", valor));
            return;
        }

        // Caso contrário, verifica o uso do cheque especial
        double deficit = valor - saldo;

        if (deficit <= limiteChequeEspecial) {
            // Zera saldo e usa parte do limite
            saldo = 0;
            limiteChequeEspecial -= deficit;

            adicionarTransacao(String.format(
                    "Saque utilizando cheque especial: R$ %.2f", valor
            ));
        } else {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente e limite do cheque especial indisponível."
            );
        }
    }

    @Override
    public void transferir(Conta destino, double valor) throws SaldoInsuficienteException {
        this.sacar(valor);  // usa lógica correta do cheque especial
        destino.depositar(valor);

        adicionarTransacao(String.format(
                "Transferência de R$ %.2f para conta %s (Titular: %s)",
                valor,
                destino.getNumero(),
                destino.getProprietario().getNome()
        ));
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(" | Limite Cheque Especial: R$ %.2f", limiteChequeEspecial);
    }
}
