package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.models.ClientePFDTO;
import br.com.felipefreitas.bancofel.services.ClientePFService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
