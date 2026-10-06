package service;

import dominio.Cliente;
import dominio.Conta;
import dominio.ContaCorrente;
import dominio.ContaPoupanca;
import excecao.ContaJaCadastradaException;
import excecao.ContaNaoEncontradaException;
import excecao.ValorInvalidoException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BancoService {

    private final Map<String, Conta> contasPorNumero;
    private final Map<String, Cliente> clientesPorCpf;
    private int sequencialConta;

    public BancoService() {
        this.contasPorNumero = new HashMap<>();
        this.clientesPorCpf = new HashMap<>();
        this.sequencialConta = 1000;
    }

    public Conta buscarConta(String numeroConta) {
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new IllegalArgumentException("O número da conta informada é inválido");
        }

        Conta conta = contasPorNumero.get(numeroConta.strip());
        if (conta == null) {
            throw new ContaNaoEncontradaException("Conta não encontrada para o número: " + numeroConta);
        }

        return conta;
    }

    public ContaCorrente abrirContaCorrente(String nome, String cpf, String email, BigDecimal limiteChequeEspecial) {
        Cliente titular = obterOuCriarCliente(nome, cpf, email);
        validarUnicidadeConta(titular.getCpf(), ContaCorrente.class);

        String numeroGerado = gerarProximoNumeroConta();
        ContaCorrente novaConta = new ContaCorrente(numeroGerado, titular, limiteChequeEspecial);

        contasPorNumero.put(numeroGerado, novaConta);
        return novaConta;
    }

    public ContaPoupanca abrirContaPoupanca(String nome, String cpf, String email) {
        Cliente titular = obterOuCriarCliente(nome, cpf, email);
        validarUnicidadeConta(titular.getCpf(), ContaPoupanca.class);

        String numeroGerado = gerarProximoNumeroConta();
        ContaPoupanca novaConta = new ContaPoupanca(numeroGerado, titular);

        contasPorNumero.put(numeroGerado, novaConta);
        return novaConta;
    }

    public void transferir(String numeroOrigem, String numeroDestino, BigDecimal valor) {
        if (numeroOrigem.strip().equals(numeroDestino.strip())) {
            throw new ValorInvalidoException("A conta de origem e destino não podem ser iguais.");
        }

        Conta contaOrigem = buscarConta(numeroOrigem);
        Conta contaDestino = buscarConta(numeroDestino);

        contaOrigem.debitarTransferencia(valor, "Transferência enviada para conta " + contaDestino.getNumero());
        contaDestino.creditarTransferencia(valor, "Transferência recebida da conta " + contaOrigem.getNumero());
    }

    private Cliente obterOuCriarCliente(String nome, String cpf, String email) {
        String cpfSanitizado = (cpf != null) ? cpf.strip() : "";

        if (clientesPorCpf.containsKey(cpfSanitizado)) {
            return clientesPorCpf.get(cpfSanitizado);
        }

        Cliente novoCliente = new Cliente(nome, cpfSanitizado, email);
        clientesPorCpf.put(cpfSanitizado, novoCliente);
        return novoCliente;
    }

    private void validarUnicidadeConta(String cpf, Class<?> tipoContaEsperada) {
        for (Conta conta : contasPorNumero.values()) {
            boolean mesmoCpf = conta.getTitular().getCpf().equalsIgnoreCase(cpf.strip());
            boolean mesmoTipo = tipoContaEsperada.isInstance(conta);

            if (mesmoCpf && mesmoTipo) {
                throw new ContaJaCadastradaException(
                        "O titular com CPF " + cpf + " já possui uma " + tipoContaEsperada.getSimpleName() + "."
                );
            }
        }
    }

    private synchronized String gerarProximoNumeroConta() {
        this.sequencialConta++;
        return String.valueOf(this.sequencialConta);
    }
}
