package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ClientePFDTO;
import br.com.felipefreitas.bancofel.services.ClientePFService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de controller usando MockMvc em modo standalone (sem subir contexto
 * Spring completo). O service é mockado e o controller é instanciado
 * manualmente via seu construtor gerado por @RequiredArgsConstructor.
 *
 * OBS: a estrutura de ClientePF/ClientePFDTO foi inferida a partir do uso em
 * ClientePFService (ver testes de serviço enviados anteriormente).
 */
@ExtendWith(MockitoExtension.class)
class ClientePFControllerTest {

    @Mock
    private ClientePFService clientePFService;

    private ClientePFController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        controller = new ClientePFController(clientePFService);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ClientePF clienteValido() {
        ClientePF cliente = new ClientePF();
        cliente.setNome("Fulano de Tal");
        cliente.setCpf("12345678900");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setLogradouro("Rua A");
        cliente.setEndereco("Casa");
        cliente.setNumero("100");
        cliente.setBairro("Centro");
        cliente.setCep("01001000");
        cliente.setCidade("Sao Paulo");
        cliente.setEstado("SP");
        cliente.setStatus(true);
        return cliente;
    }

    private ClientePFDTO dtoDe(ClientePF cliente) {
        return ClientePFDTO.builder()
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .dataNascimento(cliente.getDataNascimento())
                .logradouro(cliente.getLogradouro())
                .endereco(cliente.getEndereco())
                .numero(cliente.getNumero())
                .cep(cliente.getCep())
                .bairro(cliente.getBairro())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .status(cliente.isStatus())
                .build();
    }

    // ---------------------------------------------------------------
    // POST /clientes/pf
    // ---------------------------------------------------------------

    @Test
    void cadastraClientePF_deveRetornar201ComCorpo() throws Exception {
        ClientePF cliente = clienteValido();
        ClientePFDTO dto = dtoDe(cliente);
        when(clientePFService.cadastrarCliente(any(ClientePF.class))).thenReturn(dto);

        mockMvc.perform(post("/clientes/pf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.nome").value("Fulano de Tal"));

        ArgumentCaptor<ClientePF> captor = ArgumentCaptor.forClass(ClientePF.class);
        verify(clientePFService).cadastrarCliente(captor.capture());
        assertEquals("12345678900", captor.getValue().getCpf());
    }

    @Test
    void cadastraClientePF_devePropagarExcecaoDoService() {
        ClientePF cliente = clienteValido();
        when(clientePFService.cadastrarCliente(any(ClientePF.class)))
                .thenThrow(new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.cadastraClientePF(cliente));
        assertEquals(ErrorEnum.CPF_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // GET /clientes/pf/{documento}
    // ---------------------------------------------------------------

    @Test
    void pesquisarPorDocumento_deveRetornar200ComCorpo() throws Exception {
        ClientePF cliente = clienteValido();
        ClientePFDTO dto = dtoDe(cliente);
        when(clientePFService.pesquisaClientePorDocumento("12345678900")).thenReturn(dto);

        mockMvc.perform(get("/clientes/pf/12345678900"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678900"));

        verify(clientePFService).pesquisaClientePorDocumento("12345678900");
    }

    @Test
    void pesquisarPorDocumento_devePropagarExcecaoQuandoNaoEncontrado() {
        when(clientePFService.pesquisaClientePorDocumento("00000000000"))
                .thenThrow(new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.pesquisarPorDocumento("00000000000"));
        assertEquals(ErrorEnum.CPF_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // PUT /clientes/pf/{documento}
    // ---------------------------------------------------------------

    @Test
    void atualizarClientePF_deveRetornar200ComCorpo() throws Exception {
        ClientePF cliente = clienteValido();
        cliente.setNome("Nome Atualizado");
        ClientePFDTO dto = dtoDe(cliente);
        when(clientePFService.atualizarDadosCliente(eq("12345678900"), any(ClientePF.class))).thenReturn(dto);

        mockMvc.perform(put("/clientes/pf/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"));

        verify(clientePFService).atualizarDadosCliente(eq("12345678900"), any(ClientePF.class));
    }

    // ---------------------------------------------------------------
    // DELETE /clientes/pf/{documento}
    // ---------------------------------------------------------------

    @Test
    void softDeleteClientePF_deveRetornar200ComCorpo() throws Exception {
        ClientePF cliente = clienteValido();
        cliente.setStatus(false);
        ClientePFDTO dto = dtoDe(cliente);
        when(clientePFService.softDeleteCliente("12345678900")).thenReturn(dto);

        mockMvc.perform(delete("/clientes/pf/12345678900"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));

        verify(clientePFService).softDeleteCliente("12345678900");
    }

    // ---------------------------------------------------------------
    // PATCH /clientes/pf/{documento}/reativar
    // ---------------------------------------------------------------

    @Test
    void reativarCliente_deveRetornar200ComCorpo() throws Exception {
        ClientePF cliente = clienteValido();
        cliente.setStatus(true);
        ClientePFDTO dto = dtoDe(cliente);
        when(clientePFService.reativarCliente("12345678900")).thenReturn(dto);

        mockMvc.perform(patch("/clientes/pf/12345678900/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));

        verify(clientePFService).reativarCliente("12345678900");
    }
}