package br.com.felipefreitas.bancofel.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    // Erros de Cliente (Faixa 0-9)
    CPF_INVALIDO(400, 0, "CPF inválido"),
    CLIENTE_JA_CADASTRADO(400, 1, "Cadastro do cliente já existente"),
    CARACTERES_ACIMA(400, 2, "Excesso de caracteres"),
    NULO_BRANCO(400, 3, "Não pode estar em branco ou nulo"),
    CPF_NULO_BRANCO(400, 4, "O CPF não pode estar em branco ou nulo"),
    DATA_NASCIMENTO_NULO_BRANCO(400, 5, "A data de nascimento não pode estar em branco ou nulo"),
    CEP_INVALIDO(400, 6, "CEP inválido"),

    // Erros de Conta (Faixa 100-109)
    SALDO_NEGATIVO_NULO(400,100,"Saldo não pode ser null ou menor que zero"),

    NUMERO_CONTA_NAO_EXISTE(401,101,"Número da conta não existe"), // 401 faz mais sentido para Não Logado

    ACESSO_NEGADO(403,104,"Você não tem permissão para acessar ou alterar este recurso"), // 403 é o padrão para Acesso Negado

    // Erros de Transação (Faixa 200+)
    SAQUE_NULO_ZERO(400,200,"Valor de saque não pode ser nulo ou menor e igual que zero"),

    SAQUE_VALOR_MAIOR_SALDO(400,201,"Valor de saque maior que saldo atual"),

    PAGAMENTO_STATUS_INVALIDO(400,202,"O pedido precisa estar em estado de pagamento para ser pago");

    private final int httpStatus;
    private final int errorCode;
    private final String errorMessage;

    ErrorEnum(int httpStatus, int errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    }
