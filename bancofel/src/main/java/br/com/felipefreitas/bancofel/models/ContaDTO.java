package br.com.felipefreitas.bancofel.models;

import br.com.felipefreitas.bancofel.entity.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {

    @NotBlank(message = "Número da conta não pode estar em branco")
    private String numeroConta;

    @NotBlank(message = "Número da agência não pode estar em branco")
    private String agencia;

    @NotNull(message = "cliente não pode ser nulo")
    private Long idCliente;
}
