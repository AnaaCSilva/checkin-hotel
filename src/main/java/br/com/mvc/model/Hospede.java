package br.com.mvc.model;

public class Hospede {

    private Long id;
    private String nome;
    private String cpf;        // gravado so com digitos
    private String telefone;
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** CPF no formato 000.000.000-00 para exibir na tela. */
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) {
            return (cpf == null) ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
             + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
