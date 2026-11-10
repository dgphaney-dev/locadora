package br.ucb.locadora.modelo;

import java.util.Objects;

/**
 * Entidade de dominio que representa o Cliente da locadora.
 *
 * Participa dos relacionamentos do sistema (1 Cliente -> N Locacoes).
 */
public class Cliente {

    private Integer id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;

    public Cliente(Integer id, String nome, String cpf, String telefone, String email) {
        this.id = id;
        setNome(nome);
        setCpf(cpf);
        setTelefone(telefone);
        this.email = email != null ? email.trim() : "";
    }

    public Cliente(String nome, String cpf, String telefone, String email) {
        this(null, nome, cpf, telefone, email);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente nao pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    /**
     * Regra de validacao: CPF deve conter exatamente 11 digitos numericos.
     */
    public void setCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("O CPF nao pode ser nulo.");
        }
        String limpo = cpf.replaceAll("\\D", "");
        if (limpo.length() != 11) {
            throw new IllegalArgumentException("CPF invalido ('" + cpf + "'). Deve conter 11 digitos.");
        }
        this.cpf = limpo;
    }

    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do cliente nao pode ser vazio.");
        }
        this.telefone = telefone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (CPF: %s) - Tel: %s", id != null ? id : 0, nome, getCpfFormatado(), telefone);
    }
}
