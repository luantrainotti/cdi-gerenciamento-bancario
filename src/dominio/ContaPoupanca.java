package dominio;

import excecao.SaldoInsuficienteException;
import excecao.ValorInvalidoException;

import java.time.YearMonth;

public class ContaPoupanca extends Conta {

    private static final double TAXA_RENDIMENTO = 0.005; // 0,5% a.m.
    private YearMonth ultimoMesRendimento;

    public ContaPoupanca(int numero, Cliente titular) {
        super(numero, titular, 0L);
    }

    @Override
    protected void validarSaque(long centavos) {
        if (centavos <= 0) {
            throw new ValorInvalidoException("O valor do saque deve ser maior que zero.");
        }
        if (getSaldoCentavos() < centavos) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta poupança.");
        }
    }

    public synchronized void aplicarRendimentoMensal() {
        YearMonth mesAtual = YearMonth.now();
        if (mesAtual.equals(ultimoMesRendimento)) {
            throw new IllegalStateException("O rendimento deste mês já foi aplicado nesta conta.");
        }

        long saldoAtual = getSaldoCentavos();
        if (saldoAtual > 0) {
            long rendimento = Math.round(saldoAtual * TAXA_RENDIMENTO);
            depositar(rendimento);
        }
        this.ultimoMesRendimento = mesAtual;
    }
}