package dominio;

import excecao.SaldoInsuficienteException;

import java.math.BigDecimal;

public class ContaCorrente extends Conta{

    public static final BigDecimal TAXA_SAQUE = new BigDecimal("2.50");
    public final BigDecimal limiteChequeEspecial;

    public ContaCorrente(String numero, Cliente titular, BigDecimal limiteChequeEspecial) {
        super(numero, titular);

        if (limiteChequeEspecial == null || limiteChequeEspecial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O limite de cheque especial não pode ser nulo ou negativo");
        }
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor, "saque");

        BigDecimal valorTotalDebito = valor.add(TAXA_SAQUE);
        BigDecimal saldoTotalDisponivel = this.saldo.add(this.limiteChequeEspecial);

        if (valorTotalDebito.compareTo(saldoTotalDisponivel) > 0) {
            throw new SaldoInsuficienteException(String.format(
                    "Saldo insuficiente para saque com taxa (R$ %.2f). Disponível com limite: R$ %.2f",
                    TAXA_SAQUE,
                    saldoTotalDisponivel
            ));
        }

        this.saldo = this.saldo.subtract(valorTotalDebito);

        String descricao = String.format("Saque de R$ %.2f (Taxa: R$ %.2f)", valor, TAXA_SAQUE);
        this.getTransacoes();
        adicionarTransacao(new Transacao(TipoTransacao.SAQUE, valorTotalDebito, descricao));
    }

    public BigDecimal getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public static BigDecimal getTaxaSaque() {
        return TAXA_SAQUE;
    }
}
