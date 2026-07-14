package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.entity.Transacao;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import br.com.felipefreitas.bancofel.models.TransacaoDTO;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import br.com.felipefreitas.bancofel.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * OBS: a estrutura de Conta/Transacao/TransacaoDTO foi inferida a partir do
 * uso feito em TransacaoService, já que essas classes não foram fornecidas.
 */
@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Conta contaComSaldo(String numero, BigDecimal saldo) {
        return Conta.builder()
                .numeroConta(numero)
                .agencia("0001")
                .saldo(saldo)
                .build();
    }

    // ---------------------------------------------------------------
    // saque
    // ---------------------------------------------------------------

    @Test
    void saque_deveLancarExcecao_quandoContaNaoExiste() {
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.saque("123456", BigDecimal.TEN));
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void saque_deveLancarExcecao_quandoValorNulo() {
        Conta conta = contaComSaldo("123456", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> transacaoService.saque("123456", null));
    }

    @Test
    void saque_deveLancarExcecao_quandoValorZeroOuNegativo() {
        Conta conta = contaComSaldo("123456", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> transacaoService.saque("123456", BigDecimal.ZERO));
    }

    @Test
    void saque_deveLancarExcecao_quandoValorMaiorQueSaldo() {
        Conta conta = contaComSaldo("123456", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> transacaoService.saque("123456", BigDecimal.valueOf(100)));
    }

    @Test
    void saque_deveRealizarSaqueComSucesso() {
        Conta conta = contaComSaldo("123456", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

        BigDecimal saldoAtualizado = transacaoService.saque("123456", BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), saldoAtualizado);
        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    // ---------------------------------------------------------------
    // deposito
    // ---------------------------------------------------------------

    @Test
    void deposito_deveLancarExcecao_quandoContaNaoExiste() {
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.deposito("123456", BigDecimal.TEN));
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deposito_deveLancarExcecao_quandoValorNulo() {
        Conta conta = contaComSaldo("123456", BigDecimal.ZERO);
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> transacaoService.deposito("123456", null));
    }

    @Test
    void deposito_deveLancarExcecao_quandoValorZeroOuNegativo() {
        Conta conta = contaComSaldo("123456", BigDecimal.ZERO);
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> transacaoService.deposito("123456", BigDecimal.valueOf(-5)));
    }

    @Test
    void deposito_deveRealizarDepositoComSucesso() {
        Conta conta = contaComSaldo("123456", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

        BigDecimal saldoAtualizado = transacaoService.deposito("123456", BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), saldoAtualizado);
        verify(contaRepository).save(conta);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    // ---------------------------------------------------------------
    // transferencia
    // ---------------------------------------------------------------

    @Test
    void transferencia_deveLancarExcecao_quandoContaOrigemNaoExiste() {
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.transferencia("111", "222", BigDecimal.TEN));
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void transferencia_deveLancarExcecao_quandoContaDestinoNaoExiste() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("222")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.transferencia("111", "222", BigDecimal.TEN));
    }

    @Test
    void transferencia_deveLancarExcecao_quandoValorNuloOuZero() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("222")).thenReturn(Optional.of(destino));

        assertThrows(RuntimeException.class, () -> transacaoService.transferencia("111", "222", BigDecimal.ZERO));
    }

    @Test
    void transferencia_deveLancarExcecao_quandoSaldoInsuficiente() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(10));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("222")).thenReturn(Optional.of(destino));

        assertThrows(RuntimeException.class,
                () -> transacaoService.transferencia("111", "222", BigDecimal.valueOf(100)));
    }

    @Test
    void transferencia_deveRealizarComSucesso() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("222")).thenReturn(Optional.of(destino));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

        TransacaoDTO dto = transacaoService.transferencia("111", "222", BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), origem.getSaldo());
        assertEquals(BigDecimal.valueOf(80), destino.getSaldo());
        assertEquals(TipoTransacao.TRANSFERENCIA, dto.getTipoTransacao());
        assertEquals("111", dto.getContaOrigem());
        assertEquals("222", dto.getContaDestino());
        verify(contaRepository).save(origem);
        verify(contaRepository).save(destino);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    // ---------------------------------------------------------------
    // pix
    // ---------------------------------------------------------------

    @Test
    void pix_deveLancarExcecao_quandoContaOrigemNaoExiste() {
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.pix("111", "chave", BigDecimal.TEN));
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void pix_deveLancarExcecao_quandoChavePixNaoExiste() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByChavePix("chave")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transacaoService.pix("111", "chave", BigDecimal.TEN));
    }

    @Test
    void pix_deveLancarExcecao_quandoContaOrigemIgualDestino() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByChavePix("chave")).thenReturn(Optional.of(origem));

        assertThrows(RuntimeException.class, () -> transacaoService.pix("111", "chave", BigDecimal.TEN));
    }

    @Test
    void pix_deveLancarExcecao_quandoValorNuloOuZero() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByChavePix("chave")).thenReturn(Optional.of(destino));

        assertThrows(RuntimeException.class, () -> transacaoService.pix("111", "chave", BigDecimal.ZERO));
    }

    @Test
    void pix_deveLancarExcecao_quandoSaldoInsuficiente() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(10));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByChavePix("chave")).thenReturn(Optional.of(destino));

        assertThrows(RuntimeException.class, () -> transacaoService.pix("111", "chave", BigDecimal.valueOf(100)));
    }

    @Test
    void pix_deveRealizarComSucesso() {
        Conta origem = contaComSaldo("111", BigDecimal.valueOf(100));
        Conta destino = contaComSaldo("222", BigDecimal.valueOf(50));
        when(contaRepository.findByNumeroConta("111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByChavePix("chave")).thenReturn(Optional.of(destino));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

        TransacaoDTO dto = transacaoService.pix("111", "chave", BigDecimal.valueOf(20));

        assertEquals(BigDecimal.valueOf(80), origem.getSaldo());
        assertEquals(BigDecimal.valueOf(70), destino.getSaldo());
        assertEquals(TipoTransacao.PIX, dto.getTipoTransacao());
        verify(contaRepository).save(origem);
        verify(contaRepository).save(destino);
    }

    // ---------------------------------------------------------------
    // listarTransacaoPorTipo
    // ---------------------------------------------------------------

    @Test
    void listarTransacaoPorTipo_deveLancarExcecao_quandoTipoNulo() {
        assertThrows(RuntimeException.class, () -> transacaoService.listarTransacaoPorTipo(null));
        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void listarTransacaoPorTipo_deveRetornarLista() {
        List<Transacao> lista = List.of(
                Transacao.builder().tipoTransacao(TipoTransacao.SAQUE).valor(BigDecimal.TEN).build());
        when(transacaoRepository.findByTipoTransacao(TipoTransacao.SAQUE)).thenReturn(lista);

        List<Transacao> resultado = transacaoService.listarTransacaoPorTipo(TipoTransacao.SAQUE);

        assertEquals(1, resultado.size());
        verify(transacaoRepository).findByTipoTransacao(TipoTransacao.SAQUE);
    }
}