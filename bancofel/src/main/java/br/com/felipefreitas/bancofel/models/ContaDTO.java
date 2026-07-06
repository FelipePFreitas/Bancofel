package br.com.felipefreitas.bancofel.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaDTO {

    @NotBlank(message = "Número da conta não pode estar em branco")
    private String numeroConta;

    @NotBlank(message = "Número da agência não pode estar em branco")
    private String agencia;

    @NotNull(message = "Número da agência não pode estar em branco")
    private BigDecimal saldo;

    @NotNull(message = "cliente não pode ser nulo")
    private Long idCliente;
}
