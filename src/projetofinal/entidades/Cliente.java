package projetofinal.entidades;

import projetofinal.utilitarios.ValidadorCliente;

import java.util.List;
import java.util.Objects;

public class Cliente {
    private String nome;
    private String cpf;
    private String endereco;
    private List<Conta> contas;

    public Cliente(String nome, String cpf, String endereco, List<Conta> contas) {

            ValidadorCliente.isClienteValido(nome,cpf,endereco);
                this.nome = nome.trim();
                this.cpf = cpf;
                this.endereco = endereco.trim();
                this.contas = contas;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
    }

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
        Cliente c2 = (Cliente) obj2;
        return Objects.equals(cpf, c2.cpf);
    }

    @Override
    public String toString() {
    return "";
    }
}


