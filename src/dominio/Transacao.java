package dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacao {
   private final LocalDateTime dataHora;
   private final TipoTransacao tipo;
   private final BigDecimal valor;
   private final String descricao;

   public Transacao(TipoTransacao tipo, BigDecimal valor, String descricao) {
      this.tipo = tipo;
      this.valor = valor;
      this.descricao = descricao;
   }

   public LocalDateTime getDataHora() {
      return dataHora;
   }

   public BigDecimal getValor() {
      return valor;
   }

   public TipoTransacao getTipo() {
      return tipo;
   }

   public String getDescricao() {
      return descricao;
   }

   @Override
   public String toString() {
      return String.format("[%s] %-22s R$ %10.2f - %s",
              dataHora.toString().substring(0, 19).replace("T", " "),
              tipo,
              valor,
              descricao);
   }
}
