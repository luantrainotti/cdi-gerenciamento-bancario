package dominio;

import excecao.ValorInvalidoException;

import java.util.ArrayList;
import java.util.List;

public abstract class Conta {

    private final int numero;
    private final Cliente titular;
    private long saldoCentavos;
    private final List<Transacao> transacoes = new ArrayList<>();

    protected Conta(int numero, Cliente titular, long saldoInicialCentavos) {
        this.numero = numero;
        this.titular = titular;
        this.saldoCentavos = saldoInicialCentavos;
    }

    public int getNumero() {
        return numero;
    }

    public Cliente getTitular() {
        return titular;
    }

    public synchronized long getSaldoCentavos() {
        return saldoCentavos;
    }

    public synchronized List<Transacao> getTransacoes() {
        return List.copyOf(transacoes);
    }

    public synchronized void depositar(long centavos) {
        if (centavos <= 0) {
            throw new ValorInvalidoException("O valor do depósito deve ser maior que zero.");
        }
        this.saldoCentavos += centavos;
        this.transacoes.add(new Transacao(TipoTransacao.DEPOSITO, centavos));
    }

    public synchronized void sacar(long centavos) {
        validarSaque(centavos);
        this.saldoCentavos -= centavos;
        this.transacoes.add(new Transacao(TipoTransacao.SAQUE, centavos));
    }

    synchronized void debitarTransferencia(long centavos) {
        validarSaque(centavos);
        this.saldoCentavos -= centavos;
        this.transacoes.add(new Transacao(TipoTransacao.TRANSFERENCIA_ENVIADA, centavos));
    }

    synchronized void creditarTransferencia(long centavos) {
        if (centavos <= 0) {
            throw new ValorInvalidoException("Valor de transferência inválido.");
        }
        this.saldoCentavos += centavos;
        this.transacoes.add(new Transacao(TipoTransacao.TRANSFERENCIA_RECEBIDA, centavos));
    }

    protected abstract void validarSaque(long centavos);
}