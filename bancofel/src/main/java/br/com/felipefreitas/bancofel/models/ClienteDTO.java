package br.com.felipefreitas.bancofel.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O sobrenome não pode estar em branco")
    private String sobrenome;

    @NotBlank(message = "O cpf não pode estar em branco")
    private String cpf;

    @NotBlank(message = "não pode estar em branco")
    private LocalDate dataNascimento;

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