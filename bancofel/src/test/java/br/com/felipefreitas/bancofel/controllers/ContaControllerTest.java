package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.services.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock
    private ContaService contaService;

    private ContaController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new ContaController(contaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------------------------------------------------------
    // GET /conta/{numeroConta}
    // ---------------------------------------------------------------

    @Test
    void consultarSaldo_deveRetornar200ComSaldo() throws Exception {
        when(contaService.consultarSaldo("123456")).thenReturn(BigDecimal.valueOf(150.50));

        mockMvc.perform(get("/conta/123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.5"));

        verify(contaService).consultarSaldo("123456");
    }

    @Test
    void consultarSaldo_devePropagarExcecaoQuandoContaNaoExiste() {
        when(contaService.consultarSaldo("000000"))
                .thenThrow(new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.consultarSaldo("000000"));
        assertEquals(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // POST /conta/{numeroConta}/chaves-pix/{chavePix}
    // ---------------------------------------------------------------

    @Test
    void cadastrarChavePix_deveRetornar201() throws Exception {
        mockMvc.perform(post("/conta/123456/chaves-pix/minhaChavePix"))
                .andExpect(status().isCreated());

        verify(contaService).cadastrarChavePix("123456", "minhaChavePix");
    }

    @Test
    void cadastrarChavePix_devePropagarExcecaoDoService() {
        org.mockito.Mockito.doThrow(new RuntimeException(ErrorEnum.LIMITE_CHAVEPIX.getErrorMessage()))
                .when(contaService).cadastrarChavePix("123456", "chaveExtra");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.cadastrarChavePix("123456", "chaveExtra"));
        assertEquals(ErrorEnum.LIMITE_CHAVEPIX.getErrorMessage(), ex.getMessage());
    }
}