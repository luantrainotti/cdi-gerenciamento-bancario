package dominio;

import excecao.SaldoInsuficienteException;
import excecao.ValorInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(String numero, Cliente titular) {
        super(numero, titular);
    }

    @Override
    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor, "saque");

        if (valor.compareTo(this.saldo) > 0) {
            throw new SaldoInsuficienteException(String.format("Saldo insuficiente para saque na poupança. Saldo atual: R$ %.2f",
                    this.saldo
            ));
        }

        this.saldo.subtract(valor);
        adicionarTransacao(new Transacao(TipoTransacao.SAQUE, valor, "Saque em conta popança"));
    }

    public void aplicarRendimento(BigDecimal taxaPercentual) {
        if (taxaPercentual == null || taxaPercentual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("A taxa de rendimento deve ser maior que zero");
        }

        BigDecimal rendimento = this.saldo
                .multiply(taxaPercentual)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        if (rendimento.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(rendimento);
            adicionarTransacao(new Transacao(
                    TipoTransacao.DEPOSITO,
                    rendimento,
                    String.format("Rendimento aplicado (%.2f%%)", taxaPercentual)
            ));
        }
    }
}
