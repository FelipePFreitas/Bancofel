package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.models.ClientePFDTO;
import br.com.felipefreitas.bancofel.services.ClientePFService;
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
@RequestMapping("/clientes/pf")
@RequiredArgsConstructor
@Tag(name = "Cadastro de cliente pessoa física", description = "Endpoint cadastro pessoa física")
public class ClientePFController {
    private final ClientePFService clientePFService;

    @PostMapping
    @Operation(summary = "Cadastrar cliente PF", description = "Cadastrar cliente PF")
    @ApiResponse(description = "Cliente PF cadastrado com sucesso")
    public ResponseEntity<@NonNull ClientePFDTO> cadastraClientePF(@RequestBody ClientePF clientePF) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePFService.cadastrarCliente(clientePF));
    }

    @GetMapping("/{documento}")
    @Operation(summary = "Buscar cliente PF por CPF", description = "Busca os dados públicos de um cliente PF pelo CPF")
    public ResponseEntity<@NonNull ClientePFDTO> pesquisarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clientePFService.pesquisaClientePorDocumento(documento));
    }

    @PutMapping("/{documento}")
    @Operation(summary = "Atualizar cliente PF", description = "Atualiza os dados de um cliente PF existente")
    public ResponseEntity<@NonNull ClientePFDTO> atualizarClientePF(@PathVariable String documento,
                                                                    @Valid @RequestBody ClientePF clientePF) {
        return ResponseEntity.ok(clientePFService.atualizarDadosCliente(documento, clientePF));
    }

    @DeleteMapping("/{documento}")
    @Operation(summary = "Desativar cliente PF", description = "Realiza o soft delete (status = false) do cliente")
    public ResponseEntity<@NonNull ClientePFDTO> softDeleteClientePF(@PathVariable String documento) {
        return ResponseEntity.ok(clientePFService.softDeleteCliente(documento));
    }

    @PatchMapping("/{documento}/reativar")
    @Operation(summary = "Reativar cliente PF", description = "Reativa o cadastro de um cliente desativado (status = true)")
    public ResponseEntity<@NonNull ClientePFDTO> reativarCliente(@PathVariable String documento) {
        return ResponseEntity.ok(clientePFService.reativarCliente(documento));
    }
}
