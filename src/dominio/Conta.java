package dominio;

import excecao.SaldoInsuficienteException;
import excecao.ValorInvalidoException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Conta {
    private final String numero;
    private final Cliente titular;
    protected BigDecimal saldo;
    private final List<Transacao> transacoes;

    public Conta(String numero, Cliente titular) {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("O número da conta não pode ser nulo ou vazio");
        }

        if (titular == null) {
            throw new IllegalArgumentException("O titular da conta não pode ser nulo");
        }

        this.numero = numero;
        this.titular = titular;
        this.saldo = BigDecimal.ZERO;
        this.transacoes = new ArrayList<>();
    }

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor, "depósito");

        this.saldo = this.saldo.add(valor);
        this.transacoes.add(new Transacao(TipoTransacao.DEPOSITO, valor, "Depósito realizado com sucesso"));
    }

    public abstract void sacar(BigDecimal valor);

    public void creditarTransferencia(BigDecimal valor, String descricaoOrigem) {

        validarValorPositivo(valor, "transferência");

        this.saldo = this.saldo.add(valor);
        this.transacoes.add(new Transacao(TipoTransacao.TRANSFERENCIA_RECEBIDA, valor, descricaoOrigem));
    }

    public void debitarTransferencia(BigDecimal valor, String descricaoDestino) {
        validarValorPositivo(valor, "transferencia");

        if (valor.compareTo(this.saldo) > 0) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente para a transferência. Saldo atual: R$ %.2f", this.saldo)
            );
        }

        this.saldo = this.saldo.subtract(valor);
        this.transacoes.add(new Transacao(TipoTransacao.TRANSFERENCIA_ENVIADA, valor, descricaoDestino));
    }

    protected void validarValorPositivo(BigDecimal valor, String operacao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor para " + operacao + " deve ser maior que zero.");
        }
    }

    protected void adicionarTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
    }

    public String getNumero() {
        return numero;
    }

    public Cliente getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public List<Transacao> getTransacoes() {
        return Collections.unmodifiableList(this.transacoes);
    }
}