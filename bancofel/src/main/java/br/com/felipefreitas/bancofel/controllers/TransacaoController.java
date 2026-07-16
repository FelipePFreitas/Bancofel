package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.models.TransacaoDTO;
import br.com.felipefreitas.bancofel.services.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/transacao")
@RequiredArgsConstructor
@Tag(name = "Serviços de transações", description = "Endpoint para controle de transações")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PutMapping("/saque/{numeroConta}")
    @Operation(summary = "Saque", description = "Realiza o saque da conta estabelecida")
    public ResponseEntity<@NonNull BigDecimal> saque(@PathVariable String numeroConta,
                                                     @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transacaoService.saque(numeroConta, valor));
    }

    @PutMapping("/deposito/{numeroConta}")
    @Operation(summary = "Deposito", description = "Realiza o deposito na conta estabelecida")
    public ResponseEntity<@NonNull BigDecimal> deposito(@PathVariable String numeroConta,
                                                        @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transacaoService.deposito(numeroConta, valor));
    }

    @PutMapping("/transferencia/{numeroConta}/para/{numeroContaDestino}")
    @Operation(summary = "Transferência", description = "Realiza a transferência de uma conta para outra conta")
    public ResponseEntity<@NonNull TransacaoDTO> transferencia(@PathVariable String numeroConta,
                                                               @PathVariable String numeroContaDestino,
                                                               @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transacaoService.transferencia(numeroConta, numeroContaDestino, valor));
    }

    @PutMapping("/pix/{numeroConta}/para/{chavePix}")
    @Operation(summary = "Pix", description = "Realiza o pix para um conta com a chave cadastrada")
    public ResponseEntity<@NonNull TransacaoDTO> pix(@PathVariable String numeroConta,
                                                               @PathVariable String chavePix,
                                                               @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(transacaoService.pix(numeroConta, chavePix, valor));
    }


}
