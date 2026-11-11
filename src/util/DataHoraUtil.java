package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DataHoraUtil {

    public static final DateTimeFormatter FORMATADOR_PADRAO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private DataHoraUtil() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada.");
    }

    public static String formatar(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "-";
        }
        return dataHora.format(FORMATADOR_PADRAO);
    }
}