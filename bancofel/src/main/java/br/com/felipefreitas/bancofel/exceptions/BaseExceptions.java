package br.com.felipefreitas.bancofel.exceptions;

import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import lombok.Getter;

@Getter
public class BaseExceptions extends RuntimeException {
    private final ErrorEnum errorEnum;

    public BaseExceptions(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());

        this.errorEnum = errorEnum;
    }

}
