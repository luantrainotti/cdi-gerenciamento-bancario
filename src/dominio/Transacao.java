package dominio;

import java.time.LocalDateTime;

public record Transacao(
        TipoTransacao tipo,
        long valorCentavos,
        LocalDateTime dataHora
) {
   public Transacao(TipoTransacao tipo, long valorCentavos) {
      this(tipo, valorCentavos, LocalDateTime.now());
   }
}