package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Cliente;
import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * OBS: a estrutura de Cliente/ClientePF/ClientePJ/Conta foi inferida a partir
 * do uso feito em ContaService, já que essas classes não foram fornecidas.
 * Assume-se que Conta possui @Builder com o campo chavesPix (Set<String>).
 */
@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta contaComChaves(Cliente cliente, Set<String> chaves) {
        return Conta.builder()
                .numeroConta("123456")
                .agencia("0001")
                .saldo(BigDecimal.ZERO)
                .cliente(cliente)
                .chavesPix(chaves)
                .build();
    }

    // ---------------------------------------------------------------
    // criarConta
    // ---------------------------------------------------------------

    @Test
    void criarConta_deveLancarExcecao_quandoSaldoNulo() {
        Cliente cliente = new ClientePF();

        assertThrows(RuntimeException.class, () -> contaService.criarConta(cliente, null));
        verifyNoInteractions(contaRepository);
    }

    @Test
    void criarConta_deveLancarExcecao_quandoSaldoNegativo() {
        Cliente cliente = new ClientePF();

        assertThrows(RuntimeException.class, () -> contaService.criarConta(cliente, BigDecimal.valueOf(-10)));
        verifyNoInteractions(contaRepository);
    }

    @Test
    void criarConta_deveGerarNovoNumero_quandoNumeroJaExiste() {
        Cliente cliente = new ClientePF();
        when(contaRepository.existsByNumeroConta(anyString())).thenReturn(true, false);
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        Conta conta = contaService.criarConta(cliente, BigDecimal.ZERO);

        assertNotNull(conta);
        verify(contaRepository, times(2)).existsByNumeroConta(anyString());
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void criarConta_deveCriarContaComSucesso() {
        Cliente cliente = new ClientePF();
        when(contaRepository.existsByNumeroConta(anyString())).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        Conta conta = contaService.criarConta(cliente, BigDecimal.ZERO);

        assertEquals("0001", conta.getAgencia());
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
        assertEquals(cliente, conta.getCliente());
        assertNotNull(conta.getNumeroConta());
    }

    // ---------------------------------------------------------------
    // gerarNumeroConta
    // ---------------------------------------------------------------

    @Test
    void gerarNumeroConta_deveGerarNumeroComSeisDigitos() {
        String numero = contaService.gerarNumeroConta();

        assertNotNull(numero);
        assertEquals(7, numero.length());
        assertTrue(numero.chars().allMatch(Character::isDigit));
    }

    // ---------------------------------------------------------------
    // consultarSaldo
    // ---------------------------------------------------------------

    @Test
    void consultarSaldo_deveLancarExcecao_quandoContaNaoExiste() {
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contaService.consultarSaldo("123456"));
    }

    @Test
    void consultarSaldo_deveRetornarSaldo_quandoContaExiste() {
        ClientePF cliente = new ClientePF();
        Conta conta = Conta.builder()
                .numeroConta("123456")
                .agencia("0001")
                .saldo(BigDecimal.TEN)
                .cliente(cliente)
                .build();
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        BigDecimal saldo = contaService.consultarSaldo("123456");

        assertEquals(BigDecimal.TEN, saldo);
    }

    // ---------------------------------------------------------------
    // cadastrarChavePix
    // ---------------------------------------------------------------

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoChaveNula() {
        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", null));
        verifyNoInteractions(contaRepository);
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoChaveBranca() {
        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "   "));
        verifyNoInteractions(contaRepository);
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoChaveMuitoLonga() {
        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "a".repeat(78)));
        verifyNoInteractions(contaRepository);
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoContaNaoExiste() {
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "chave@teste.com"));
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoLimiteChavesPFAtingido() {
        ClientePF cliente = new ClientePF();
        Set<String> chaves = new HashSet<>(Set.of("c1", "c2", "c3", "c4", "c5"));
        Conta conta = contaComChaves(cliente, chaves);
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "novaChave"));
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoLimiteChavesPJAtingido() {
        ClientePJ cliente = new ClientePJ();
        Set<String> chaves = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            chaves.add("chave" + i);
        }
        Conta conta = contaComChaves(cliente, chaves);
        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));

        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "novaChave"));
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoChaveJaPertenceOutraConta() {
        ClientePF cliente = new ClientePF();
        Conta conta = contaComChaves(cliente, new HashSet<>());
        Conta outraConta = contaComChaves(new ClientePF(), new HashSet<>(Set.of("chave@teste.com")));

        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));
        when(contaRepository.findByChavePix("chave@teste.com")).thenReturn(Optional.of(outraConta));

        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "chave@teste.com"));
    }

    @Test
    void cadastrarChavePix_deveLancarExcecao_quandoChaveJaCadastradaNaMesmaConta() {
        ClientePF cliente = new ClientePF();
        Conta conta = contaComChaves(cliente, new HashSet<>(Set.of("chave@teste.com")));

        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));
        when(contaRepository.findByChavePix("chave@teste.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contaService.cadastrarChavePix("123456", "chave@teste.com"));
    }

    @Test
    void cadastrarChavePix_deveCadastrarComSucesso() {
        ClientePF cliente = new ClientePF();
        Conta conta = contaComChaves(cliente, new HashSet<>());

        when(contaRepository.findByNumeroConta("123456")).thenReturn(Optional.of(conta));
        when(contaRepository.findByChavePix("novaChave")).thenReturn(Optional.empty());
        when(contaRepository.save(conta)).thenReturn(conta);

        assertDoesNotThrow(() -> contaService.cadastrarChavePix("123456", "novaChave"));
        assertTrue(conta.getChavesPix().contains("novaChave"));
        verify(contaRepository).save(conta);
    }
}