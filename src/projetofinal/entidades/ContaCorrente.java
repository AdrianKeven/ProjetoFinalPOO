package projetofinal.entidades;

import projetofinal.utilitarios.SaldoInsuficienteException;

public class ContaCorrente extends Conta {
    private double limiteChequeEspecial;

    public ContaCorrente(String numero, Cliente proprietario, double limiteChequeEspecial) {
        super(numero, proprietario);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    @Override
    public void depositar(double valor) {
        if(valor > 0) {
            this.saldo += valor;
            adicionarTransacao(String.format("Deposito: R$ %.2f", valor));
        }else{
            throw new IllegalArgumentException("Valor de deposito deve ser maior que 0");
        }
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if(valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser maior que 0");
        }

        if(valor <= this.saldo){
            this.saldo -=valor;
            System.out.printf("Valor sacado: R$ %.2f%n", valor);
            System.out.println(this);
            adicionarTransacao(String.format("Saque: R$ %.2f", valor));
        } else {
            double valorFaltante = valor - saldo;
            if(valorFaltante <= this.limiteChequeEspecial){
                this.saldo = 0;
                this.limiteChequeEspecial -= valorFaltante;
                System.out.printf("Valor sacado: R$ %.2f%n", valor);
                System.out.println(this);
                adicionarTransacao(String.format("Saque: R$ %.2f", valor));
            } else {
                throw new SaldoInsuficienteException("Saldo insuficiente e limite do cheque menor que o valor sacado");
            }
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

    @Override
    public String toString() {
        return super.toString() +
                String.format(" | Limite Cheque Especial: R$ %.2f", this.limiteChequeEspecial);
    }

}
