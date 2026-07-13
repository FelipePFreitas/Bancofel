package br.com.felipefreitas.bancofel.models;

import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransacaoDTO {

    @NotNull(message = "Tipo da transação não pode nulo")
    private TipoTransacao tipoTransacao;

    @NotNull(message = "Valor da transação não pode ser nulo")
    @Positive(message = "Valor não pode ser menor ou igual a 0")
    private BigDecimal valor;

    private LocalDateTime dataHoraTransacao;

    @NotNull(message = "Conta de origem não pode ser nulo")
    private String contaOrigem;


    private String contaDestino;
}
