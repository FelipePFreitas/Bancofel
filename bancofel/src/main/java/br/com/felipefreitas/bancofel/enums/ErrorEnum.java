package br.com.felipefreitas.bancofel.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    // Erros de Cliente (Faixa 0-9)
    CPF_INVALIDO(400, 0, "CPF inválido"),
    CLIENTE_JA_CADASTRADO(400, 1, "Cadastro do já existente"),
    CARACTERES_ACIMA(400, 2, "Excesso de caracteres"),
    NULO_BRANCO(400, 3, "Não pode estar em branco ou nulo"),
    CPF_NULO_BRANCO(400, 4, "O CPF não pode estar em branco ou nulo"),
    DATA_NASCIMENTO_NULO_BRANCO(400, 5, "A data de nascimento não pode estar em branco ou nulo"),
    CEP_INVALIDO(400, 6, "CEP inválido"),

    // Erros de Funcionário / Acesso Geral (Faixa 100-109)
    FUNCIONARIO_NAO_ENCONTRADO(400,100,"Funcionário não encontrado"),

    CLIENTE_NAO_LOGADO(401,101,"Cliente não está autenticado no sistema"), // 401 faz mais sentido para Não Logado

    ACESSO_NEGADO(403,104,"Você não tem permissão para acessar ou alterar este recurso"), // 403 é o padrão para Acesso Negado

    // Erros de Pedido e Fluxo (Faixa 200+)
    PEDIDO_NAO_ENCONTRADO(400,200,"Pedido não encontrado"),

    PEDIDO_COM_STATUS_INVALIDO(400,201,"O status atual do pedido não permite realizar esta operação"),

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
