package dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transacao {
   private final LocalDateTime dataHora;
   private final TipoTransacao tipo;
   private final BigDecimal valor;
   private final String descricao;

   public Transacao(TipoTransacao tipo, BigDecimal valor, String descricao) {
      if (tipo == null) {
         throw new IllegalArgumentException("O tipo da transação não pode ser nulo.");
      }

      if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
         throw new IllegalArgumentException("O valor da transação deve ser positivo");
      }

      this.dataHora = LocalDateTime.now();
      this.tipo = tipo;
      this.valor = valor;
      this.descricao = (descricao != null && !descricao.isBlank()) ? descricao : "Sem descrição";
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
      DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
      return String.format("[%s] %-23s | R$ %8.2f | %s",
              dataHora.format(formato),
              tipo,
              valor,
              descricao);
   }
}
