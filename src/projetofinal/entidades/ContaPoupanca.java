package projetofinal.entidades;

import projetofinal.utilitarios.SaldoInsuficienteException;

public class ContaPoupanca extends Conta{
    private double taxaRendimentoMensal;

    public ContaPoupanca(String numero, Cliente proprietario, double taxaRendimentoMensal) {
        super(numero, proprietario);
        this.taxaRendimentoMensal = taxaRendimentoMensal;
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

    public void renderJuros(){
        this.saldo *= (1+this.taxaRendimentoMensal);
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(" | Taxa de Rendimento(mensal): R$ %.2f", this.taxaRendimentoMensal);

    }
}
