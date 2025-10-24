package projetofinal.entidades;

import java.util.List;

public abstract class Conta {
    private String numero;
    protected double saldo;
    private Cliente proprietario;
    private List<String> historicoTransacoes;

}
