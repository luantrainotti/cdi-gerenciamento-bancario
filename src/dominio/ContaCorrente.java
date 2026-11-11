package dominio;

import excecao.SaldoInsuficienteException;

public class ContaCorrente extends Conta {

    private final long limiteChequeEspecialCentavos;
    private static final long TAXA_OPERACAO_CENTAVOS = 100L; // R$ 1,00 de taxa por saque

    public ContaCorrente(int numero, Cliente titular, long limiteChequeEspecialCentavos) {
        super(numero, titular, 0L);
        this.limiteChequeEspecialCentavos = limiteChequeEspecialCentavos;
    }

    public long getLimiteChequeEspecialCentavos() {
        return limiteChequeEspecialCentavos;
    }

    @Override
    public synchronized void sacar(long centavos) {
        long custoTotal = centavos + TAXA_OPERACAO_CENTAVOS;
        validarSaque(custoTotal);
        super.sacar(centavos);
    }

    @Override
    protected void validarSaque(long centavosNecessarios) {
        if (getSaldoCentavos() + limiteChequeEspecialCentavos < centavosNecessarios) {
            throw new SaldoInsuficienteException("Saldo e limite de cheque especial insuficientes para a operação.");
        }
    }
}