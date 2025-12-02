package entidades;

import utilitarios.ValidadorCliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String nome;
    private final String cpf;
    private String endereco;
    private final List<Conta> contas;

    public Cliente(String nome, String cpf, String endereco) {

        ValidadorCliente.isClienteValido(nome, cpf, endereco);

        this.nome = nome.trim();
        this.cpf = cpf.trim();
        this.endereco = endereco.trim();
        this.contas = new ArrayList<>();
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
        this.nome = nome.trim();
    }

    public void setEndereco(String endereco){
        this.endereco = endereco.trim();
    }

    public void adicionarConta(Conta conta) {
        if (conta != null && !contas.contains(conta)) {
            this.contas.add(conta);
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cliente outro = (Cliente) obj;
        return Objects.equals(cpf, outro.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente {")
                .append("Nome='").append(nome).append('\'')
                .append(", CPF='").append(cpf).append('\'')
                .append(", Endereço='").append(endereco).append('\'');

        sb.append(", Contas=[");
        for (Conta conta : contas) {
            sb.append(conta).append(", ");
        }
        if (!contas.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append("]}");

        return sb.toString();
    }
}
