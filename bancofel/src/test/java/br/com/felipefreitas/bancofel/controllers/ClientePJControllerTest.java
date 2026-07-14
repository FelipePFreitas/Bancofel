package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ClientePJDTO;
import br.com.felipefreitas.bancofel.services.ClientePJService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Testes de controller usando MockMvc em modo standalone. O @Valid nos
 * endpoints de cadastro/atualização não é exercitado aqui, pois em modo
 * standalone sem um validador configurado ele não é acionado por padrão;
 * as regras de negócio já são cobertas nos testes de ClientePJService.
 *
 * OBS: estrutura de ClientePJ/ClientePJDTO inferida a partir do uso em
 * ClientePJService.
 */
@ExtendWith(MockitoExtension.class)
class ClientePJControllerTest {

    @Mock
    private ClientePJService clientePJService;

    private ClientePJController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        controller = new ClientePJController(clientePJService);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ClientePJ clienteValido() {
        ClientePJ cliente = new ClientePJ();
        cliente.setNome("Empresa Teste LTDA");
        cliente.setCnpj("12345678000199");
        cliente.setInscricaoEstadual("123456789");
        cliente.setLogradouro("Av. Central");
        cliente.setEndereco("Sala 10");
        cliente.setNumero("500");
        cliente.setBairro("Centro");
        cliente.setCep("01001000");
        cliente.setCidade("Sao Paulo");
        cliente.setEstado("SP");
        cliente.setStatus(true);
        return cliente;
    }

    private ClientePJDTO dtoDe(ClientePJ cliente) {
        return ClientePJDTO.builder()
                .nome(cliente.getNome())
                .cnpj(cliente.getCnpj())
                .inscricaoEstadual(cliente.getInscricaoEstadual())
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
    // POST /clientes/pj
    // ---------------------------------------------------------------

    @Test
    void cadastraClientePJ_deveRetornar201ComCorpo() throws Exception {
        ClientePJ cliente = clienteValido();
        ClientePJDTO dto = dtoDe(cliente);
        when(clientePJService.cadastrarCliente(any(ClientePJ.class))).thenReturn(dto);

        mockMvc.perform(post("/clientes/pj")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cnpj").value("12345678000199"))
                .andExpect(jsonPath("$.nome").value("Empresa Teste LTDA"));

        ArgumentCaptor<ClientePJ> captor = ArgumentCaptor.forClass(ClientePJ.class);
        verify(clientePJService).cadastrarCliente(captor.capture());
        assertEquals("12345678000199", captor.getValue().getCnpj());
    }

    @Test
    void cadastraClientePJ_devePropagarExcecaoDoService() {
        ClientePJ cliente = clienteValido();
        when(clientePJService.cadastrarCliente(any(ClientePJ.class)))
                .thenThrow(new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.cadastraClientePJ(cliente));
        assertEquals(ErrorEnum.CNPJ_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // GET /clientes/pj/{documento}
    // ---------------------------------------------------------------

    @Test
    void pesquisarPorDocumento_deveRetornar200ComCorpo() throws Exception {
        ClientePJ cliente = clienteValido();
        ClientePJDTO dto = dtoDe(cliente);
        when(clientePJService.pesquisaClientePorDocumento("12345678000199")).thenReturn(dto);

        mockMvc.perform(get("/clientes/pj/12345678000199"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value("12345678000199"));

        verify(clientePJService).pesquisaClientePorDocumento("12345678000199");
    }

    @Test
    void pesquisarPorDocumento_devePropagarExcecaoQuandoNaoEncontrado() {
        when(clientePJService.pesquisaClientePorDocumento("00000000000000"))
                .thenThrow(new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.pesquisarPorDocumento("00000000000000"));
        assertEquals(ErrorEnum.CNPJ_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // PUT /clientes/pj/{documento}
    // ---------------------------------------------------------------

    @Test
    void atualizarClientePJ_deveRetornar200ComCorpo() throws Exception {
        ClientePJ cliente = clienteValido();
        cliente.setNome("Nome Atualizado LTDA");
        ClientePJDTO dto = dtoDe(cliente);
        when(clientePJService.atualizarDadosCliente(eq("12345678000199"), any(ClientePJ.class))).thenReturn(dto);

        mockMvc.perform(put("/clientes/pj/12345678000199")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado LTDA"));

        verify(clientePJService).atualizarDadosCliente(eq("12345678000199"), any(ClientePJ.class));
    }

    // ---------------------------------------------------------------
    // DELETE /clientes/pj/{documento}
    // ---------------------------------------------------------------

    @Test
    void softDeleteClientePj_deveRetornar200ComCorpo() throws Exception {
        ClientePJ cliente = clienteValido();
        cliente.setStatus(false);
        ClientePJDTO dto = dtoDe(cliente);
        when(clientePJService.softDeleteCliente("12345678000199")).thenReturn(dto);

        mockMvc.perform(delete("/clientes/pj/12345678000199"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));

        verify(clientePJService).softDeleteCliente("12345678000199");
    }

    // ---------------------------------------------------------------
    // PATCH /clientes/pj/{documento}/reativar
    // ---------------------------------------------------------------

    @Test
    void reativarCliente_deveRetornar200ComCorpo() throws Exception {
        ClientePJ cliente = clienteValido();
        cliente.setStatus(true);
        ClientePJDTO dto = dtoDe(cliente);
        when(clientePJService.reativarCliente("12345678000199")).thenReturn(dto);

        mockMvc.perform(patch("/clientes/pj/12345678000199/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));

        verify(clientePJService).reativarCliente("12345678000199");
    }
}