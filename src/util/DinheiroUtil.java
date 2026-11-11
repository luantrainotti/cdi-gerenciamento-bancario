package util;

import excecao.ValorInvalidoException;

public final class DinheiroUtil {

    private DinheiroUtil() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada.");
    }

    /**
     * Converte texto monetário (ex: "10", "10.5", "10,50") diretamente para centavos em long.
     * Rejeita nulos, vazios ou números com mais de duas casas decimais.
     */
    public static long converterParaCentavos(String input) {
        if (input == null || input.isBlank()) {
            throw new ValorInvalidoException("O valor monetário não pode ser vazio.");
        }

        String texto = input.trim().replace(',', '.');

        if (!texto.matches("^\\d+(\\.\\d{1,2})?$")) {
            throw new ValorInvalidoException("Formato inválido. Use apenas números com até 2 casas decimais (ex: 10.50 ou 10,50).");
        }

        String[] partes = texto.split("\\.");
        long reais = Long.parseLong(partes[0]);
        long centavos = 0;

        if (partes.length > 1) {
            String fracao = partes[1];
            centavos = fracao.length() == 1 ? Long.parseLong(fracao) * 10 : Long.parseLong(fracao);
        }

        return (reais * 100) + centavos;
    }

    public static String formatar(long centavos) {
        long reais = centavos / 100;
        long restoCentavos = Math.abs(centavos % 100);
        return String.format("R$ %d,%02d", reais, restoCentavos);
    }
}