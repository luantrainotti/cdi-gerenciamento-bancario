package dominio;

public class Cliente {
    private final String nome;
    private final String cpf;
    private String email;

    public Cliente(String nome, String cpf, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser nulo ou vazio.");
        }

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo ou vazio.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou vazio.");
        }

        this.nome = nome.strip();
        this.cpf = cpf.strip();
        this.email = email.strip();
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O email não pode ser vazio");
        }

        this.email = email.strip();
    }

    @Override
    public String toString() {
        return String.format("Cliente: %s | CPF: %s | E-mail: %s", nome, cpf, email);
    }
}