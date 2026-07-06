package br.com.felipefreitas.bancofel.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientePJDTO {
    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O cpf não pode estar em branco")
    private String cnpj;

    @NotBlank(message = "não pode estar em branco")
    private String inscricaoEstadual;

    @NotBlank(message = "não pode estar em branco")
    private String logradouro;

    @NotBlank(message = "não pode estar em branco")
    private String endereco;

    @NotBlank(message = "não pode estar em branco")
    private String numero;

    @NotBlank(message = "não pode estar em branco")
    private String bairro;

    @NotBlank(message = "não pode estar em branco")
    private String cep;

    @NotBlank(message = "não pode estar em branco")
    private String cidade;

    @NotBlank(message = "não pode estar em branco")
    private String estado;

    @NotNull(message = "Status não pode ser nulo")
    private boolean status = true;
}
