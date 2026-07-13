package br.com.felipefreitas.bancofel.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    // Erros de Cliente (Faixa 0-9) - 400 Bad Request (Dados mal formatados)
    CPF_INVALIDO(400, 0, "CPF inválido"),
    CNPJ_INVALIDO(400, 0, "CNPJ inválido"),
    CLIENTE_JA_CADASTRADO(400, 1, "Cadastro do cliente já existente"),
    CARACTERES_ACIMA(400, 2, "Excesso de caracteres"),
    NULO_BRANCO(400, 3, "Não pode estar em branco ou nulo"),
    CPF_NULO_BRANCO(400, 4, "O CPF não pode estar em branco ou nulo"),
    DATA_NASCIMENTO_NULO_BRANCO(400, 5, "A data de nascimento não pode estar em branco ou nulo"),
    CEP_INVALIDO(400, 6, "CEP inválido"),
    TIPO_CLIENTE_INVALIDO(400, 7, "Tipo de cliente inválido"),
    CNPJ_NULO_BRANCO(400, 8, "O CPF não pode estar em branco ou nulo"),

    // Erros de Conta (Faixa 100-109)
    SALDO_NEGATIVO_NULO(400, 100, "Saldo não pode ser null ou menor que zero"),
    NUMERO_CONTA_NAO_EXISTE(404, 101, "Número da conta não existe"), // ✨ Corrigido para 404 Not Found
    SALDO_INSUFICIENTE(422, 102, "Saldo insuficiente para transferência"),
    LIMITE_CHAVEPIX(422, 103, "Limite máximo de chaves atingido"),
    CHAVEPIX_JACADASTRADA(422, 104, "Essa chave pix já foi cadastrada nessa conta"),
    CHAVEPIX_INEXISTENTE(422, 105, "Chave pix não existe"),


    // Erros de Transação (Faixa 200+)
    SAQUE_NULO_ZERO(400, 200, "Valor de saque não pode ser nulo ou menor e igual que zero"),
    SAQUE_VALOR_MAIOR_SALDO(422, 201, "Valor de saque maior que saldo atual"), // ✨ Corrigido para 422 Unprocessable Entity
    DEPOSITO_NULO_ZERO(400, 202, "Valor de deposito não pode ser nulo ou menor e igual que zero"),
    TIPO_TRANSACAO_INEXISTENTE(400, 203, "Tipo de transação inexistente");

    private final int httpStatus;
    private final int errorCode;
    private final String errorMessage;

    ErrorEnum(int httpStatus, int errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}