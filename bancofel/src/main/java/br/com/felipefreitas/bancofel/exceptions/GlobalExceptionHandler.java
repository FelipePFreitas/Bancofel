package br.com.felipefreitas.bancofel.exceptions;

import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseExceptions.class)
    public ResponseEntity<ProblemDetail> handleBaseException(BaseExceptions ex) {
        ErrorEnum error = ex.getErrorEnum();

        // Cria o padrão oficial do Spring
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(error.getHttpStatus()),
                error.getErrorMessage()
        );

        // Adiciona o seu código de erro customizado como uma propriedade extra
        problemDetail.setProperty("errorCode", error.getErrorCode());
        problemDetail.setTitle("Erro na Regra de Negócio");

        return ResponseEntity.status(error.getHttpStatus()).body(problemDetail);
    }
}