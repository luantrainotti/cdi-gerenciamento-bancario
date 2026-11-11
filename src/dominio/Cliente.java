package dominio;

import excecao.ValorInvalidoException;

public record Cliente(String nome, String cpf) {

    public Cliente {
        if (nome == null || nome.isBlank()) {
            throw new ValorInvalidoException("O nome do cliente não pode ser vazio.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new ValorInvalidoException("O CPF não pode ser vazio.");
        }
        // Remove pontuações e mantém apenas os dígitos
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new ValorInvalidoException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
    }
}