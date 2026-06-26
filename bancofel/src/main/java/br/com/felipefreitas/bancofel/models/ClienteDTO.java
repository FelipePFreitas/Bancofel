package br.com.felipefreitas.bancofel.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "O não pode estar em branco")
    private String dataNascimento;

    @NotBlank(message = "O não pode estar em branco")
    private String logradouro;

    @NotBlank(message = "O não pode estar em branco")
    private String endereco;

    @NotBlank(message = "O não pode estar em branco")
    private String numero;

    @NotBlank(message = "O não pode estar em branco")
    private String bairro;

    @NotBlank(message = "O não pode estar em branco")
    private String cep;

    @NotBlank(message = "O não pode estar em branco")
    private String cidade;

    @NotBlank(message = "O não pode estar em branco")
    private String estado;

}