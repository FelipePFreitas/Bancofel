package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.models.ClientePJDTO;
import br.com.felipefreitas.bancofel.services.ClientePJService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/clientes/pj")
@RequiredArgsConstructor
@Tag(name = "Cadastro de cliente pessoa jurídica", description = "Endpoint cadastro pessoa jurídica")
public class ClientePJController {
    private final ClientePJService clientePJService;

    @PostMapping
    @Operation(summary = "Cadastrar cliente PJ", description = "Cadastrar cliente PJ")
    @ApiResponse(description = "Cliente PJ cadastrado com sucesso")
    public ResponseEntity<@NonNull ClientePJDTO> cadastraClientePJ(@Valid @RequestBody ClientePJ clientePJ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePJService.cadastrarCliente(clientePJ));
    }

    @GetMapping("/{documento}")
    @Operation(summary = "Buscar cliente PJ por CNPJ", description = "Busca os dados públicos de um cliente PJ pelo " +
            "CNPJ")
    public ResponseEntity<@NonNull ClientePJDTO> pesquisarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clientePJService.pesquisaClientePorDocumento(documento));
    }

    @PutMapping("/{documento}")
    @Operation(summary = "Atualizar cliente PJ", description = "Atualiza os dados de um cliente PJ existente")
    public ResponseEntity<@NonNull ClientePJDTO> atualizarClientePJ(@PathVariable String documento,
                                                                    @Valid @RequestBody ClientePJ clientePJ) {
        return ResponseEntity.ok(clientePJService.atualizarDadosCliente(documento, clientePJ));
    }

    @DeleteMapping("/{documento}")
    @Operation(summary = "Desativar cliente PJ", description = "Realiza o soft delete (status = false) do cliente")
    public ResponseEntity<@NonNull ClientePJDTO> softDeleteClientePj(@PathVariable String documento) {
        return ResponseEntity.ok(clientePJService.softDeleteCliente(documento));
    }

    @PatchMapping("/{documento}/reativar")
    @Operation(summary = "Reativar cliente PJ", description = "Reativa o cadastro de um cliente desativado (status = " +
            "true)")
    public ResponseEntity<@NonNull ClientePJDTO> reativarCliente(@PathVariable String documento) {
        return ResponseEntity.ok(clientePJService.reativarCliente(documento));
    }
}
