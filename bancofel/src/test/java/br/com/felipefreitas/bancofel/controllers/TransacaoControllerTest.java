package br.com.felipefreitas.bancofel.controllers;

import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import br.com.felipefreitas.bancofel.models.TransacaoDTO;
import br.com.felipefreitas.bancofel.services.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ATENÇÃO - possível bug encontrado em TransacaoController:
 * o campo `TransacaoService transacaoService;` não é `private final` nem
 * anotado com @Autowired. Como a classe usa @RequiredArgsConstructor (que só
 * gera parâmetros de construtor para campos `final`), o Lombok gera um
 * construtor SEM argumentos, e o Spring nunca injeta o service nesse campo.
 * Em tempo de execução isso resulta em NullPointerException ao chamar
 * qualquer endpoint. Recomenda-se alterar o campo para
 * `private final TransacaoService transacaoService;`.
 *
 * Como o teste não pode usar o construtor (não existe parâmetro), o mock é
 * injetado via reflection (ReflectionTestUtils), simulando o cenário em que
 * a injeção de dependência funcionaria corretamente.
 */
@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    @Mock
    private TransacaoService transacaoService;

    private TransacaoController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new TransacaoController();
        ReflectionTestUtils.setField(controller, "transacaoService", transacaoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------------------------------------------------------
    // PUT /transacao/saque/{numeroConta}
    // ---------------------------------------------------------------

    @Test
    void saque_deveRetornar200ComSaldoAtualizado() throws Exception {
        when(transacaoService.saque("123456", BigDecimal.valueOf(50))).thenReturn(BigDecimal.valueOf(50));

        mockMvc.perform(put("/transacao/saque/123456").param("valor", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));

        verify(transacaoService).saque("123456", BigDecimal.valueOf(50));
    }

    @Test
    void saque_devePropagarExcecaoDoService() {
        when(transacaoService.saque("123456", BigDecimal.valueOf(1000)))
                .thenThrow(new RuntimeException(ErrorEnum.SAQUE_VALOR_MAIOR_SALDO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.saque("123456", BigDecimal.valueOf(1000)));
        assertEquals(ErrorEnum.SAQUE_VALOR_MAIOR_SALDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // PUT /transacao/deposito/{numeroConta}
    // ---------------------------------------------------------------

    @Test
    void deposito_deveRetornar200ComSaldoAtualizado() throws Exception {
        when(transacaoService.deposito("123456", BigDecimal.valueOf(100))).thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(put("/transacao/deposito/123456").param("valor", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(transacaoService).deposito("123456", BigDecimal.valueOf(100));
    }

    @Test
    void deposito_devePropagarExcecaoDoService() {
        when(transacaoService.deposito("123456", BigDecimal.ZERO))
                .thenThrow(new RuntimeException(ErrorEnum.DEPOSITO_NULO_ZERO.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.deposito("123456", BigDecimal.ZERO));
        assertEquals(ErrorEnum.DEPOSITO_NULO_ZERO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // PUT /transacao/transferencia/{numeroConta}/para/{numeroContaDestino}
    // ---------------------------------------------------------------

    @Test
    void transferencia_deveRetornar200ComDto() throws Exception {
        TransacaoDTO dto = TransacaoDTO.builder()
                .tipoTransacao(TipoTransacao.TRANSFERENCIA)
                .valor(BigDecimal.valueOf(30))
                .contaOrigem("111")
                .contaDestino("222")
                .build();
        when(transacaoService.transferencia("111", "222", BigDecimal.valueOf(30))).thenReturn(dto);

        mockMvc.perform(put("/transacao/transferencia/111/para/222").param("valor", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaOrigem").value("111"))
                .andExpect(jsonPath("$.contaDestino").value("222"));

        verify(transacaoService).transferencia("111", "222", BigDecimal.valueOf(30));
    }

    @Test
    void transferencia_devePropagarExcecaoDoService() {
        when(transacaoService.transferencia("111", "222", BigDecimal.valueOf(1000)))
                .thenThrow(new RuntimeException(ErrorEnum.SALDO_INSUFICIENTE.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.transferencia("111", "222", BigDecimal.valueOf(1000)));
        assertEquals(ErrorEnum.SALDO_INSUFICIENTE.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // PUT /transacao/pix/{numeroConta}/para/{chavePix}
    // ---------------------------------------------------------------

    @Test
    void pix_deveRetornar200ComDto() throws Exception {
        TransacaoDTO dto = TransacaoDTO.builder()
                .tipoTransacao(TipoTransacao.PIX)
                .valor(BigDecimal.valueOf(20))
                .contaOrigem("111")
                .contaDestino("222")
                .build();
        when(transacaoService.pix("111", "chavePix", BigDecimal.valueOf(20))).thenReturn(dto);

        mockMvc.perform(put("/transacao/pix/111/para/chavePix").param("valor", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaOrigem").value("111"))
                .andExpect(jsonPath("$.contaDestino").value("222"));

        verify(transacaoService).pix("111", "chavePix", BigDecimal.valueOf(20));
    }

    @Test
    void pix_devePropagarExcecaoDoService() {
        when(transacaoService.pix("111", "chaveInexistente", BigDecimal.valueOf(20)))
                .thenThrow(new RuntimeException(ErrorEnum.CHAVEPIX_INEXISTENTE.getErrorMessage()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.pix("111", "chaveInexistente", BigDecimal.valueOf(20)));
        assertEquals(ErrorEnum.CHAVEPIX_INEXISTENTE.getErrorMessage(), ex.getMessage());
    }
}