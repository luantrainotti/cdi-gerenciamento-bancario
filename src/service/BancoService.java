package service;

import dominio.Conta;
import excecao.ContaJaCadastradaException;
import excecao.ContaNaoEncontradaException;
import excecao.ValorInvalidoException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BancoService {

    private final Map<Integer, Conta> contasPorNumero = new ConcurrentHashMap<>();
    private final Map<String, Integer> contasPorCpf = new ConcurrentHashMap<>();
    private final AtomicInteger sequencialConta = new AtomicInteger(1001);

    public int gerarNovoNumeroConta() {
        return sequencialConta.getAndIncrement();
    }

    public synchronized void cadastrarConta(Conta conta) {
        if (conta == null) {
            throw new ValorInvalidoException("Conta não pode ser nula.");
        }
        String cpf = conta.getTitular().cpf();
        if (contasPorCpf.containsKey(cpf)) {
            throw new ContaJaCadastradaException("Já existe uma conta ativa para o CPF informado.");
        }

        contasPorNumero.put(conta.getNumero(), conta);
        contasPorCpf.put(cpf, conta.getNumero());
    }

    public Conta buscarPorNumero(int numero) {
        Conta conta = contasPorNumero.get(numero);
        if (conta == null) {
            throw new ContaNaoEncontradaException("Conta de número " + numero + " não foi encontrada.");
        }
        return conta;
    }

    public Conta buscarPorCpf(String cpf) {
        if (cpf == null) {
            throw new ValorInvalidoException("CPF não pode ser nulo.");
        }
        String cpfNormalizado = cpf.replaceAll("\\D", "");
        Integer numero = contasPorCpf.get(cpfNormalizado);
        if (numero == null) {
            throw new ContaNaoEncontradaException("Nenhuma conta vinculada ao CPF " + cpfNormalizado);
        }
        return buscarPorNumero(numero);
    }

    public void transferir(int numeroOrigem, int numeroDestino, long centavos) {
        if (numeroOrigem == numeroDestino) {
            throw new ValorInvalidoException("A conta de destino não pode ser igual à de origem.");
        }
        if (centavos <= 0) {
            throw new ValorInvalidoException("O valor da transferência deve ser maior que zero.");
        }

        Conta origem = buscarPorNumero(numeroOrigem);
        Conta destino = buscarPorNumero(numeroDestino);

        // Bloqueio ordenado determinístico por ID para prevenir deadlocks
        Conta primeiroLock = origem.getNumero() < destino.getNumero() ? origem : destino;
        Conta segundoLock = origem.getNumero() < destino.getNumero() ? destino : origem;

        synchronized (primeiroLock) {
            synchronized (segundoLock) {
                origem.debitarTransferencia(centavos);
                destino.creditarTransferencia(centavos);
            }
        }
    }
}