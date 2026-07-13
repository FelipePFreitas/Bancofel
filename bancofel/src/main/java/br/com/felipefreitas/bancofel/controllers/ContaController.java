package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.services.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
@Tag(name = "Serviços de conta", description = "Endpoint para controle de conta")
public class ContaController {
    private final ContaService contaService;

    @GetMapping("/{numeroConta}")
    @Operation(summary = "Consultar saldo da conta", description = "Consulta de saldo da conta")
    public ResponseEntity<@NonNull BigDecimal> consultarSaldo(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.consultarSaldo(numeroConta));
    }

    @PostMapping("/{numeroConta}/chaves-pix/{chavePix}")
    @Operation(summary = "Cadastrar chaves pix", description = "Cadastro de chave pix")
    public ResponseEntity<@NonNull Void> cadastrarChavePix(@PathVariable String numeroConta,
                                                           @PathVariable String chavePix) {
        contaService.cadastrarChavePix(numeroConta,chavePix);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
