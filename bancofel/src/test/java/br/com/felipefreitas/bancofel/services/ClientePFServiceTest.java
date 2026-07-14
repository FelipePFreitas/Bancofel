package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ClientePFDTO;
import br.com.felipefreitas.bancofel.repository.ClientePFRepository;
import br.com.felipefreitas.bancofel.utils.CEPUtil;
import br.com.felipefreitas.bancofel.utils.CPFUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * OBS: como as classes de entidade/DTO/util não foram fornecidas neste chat,
 * a estrutura (getters/setters, builders) foi inferida a partir do uso feito
 * dentro de ClientePFService. Pequenos ajustes de nomes podem ser necessários
 * caso a implementação real difira do inferido.
 */
@ExtendWith(MockitoExtension.class)
class ClientePFServiceTest {

    @Mock
    private ContaService contaService;

    @Mock
    private ClientePFRepository clientePFRepository;

    @InjectMocks
    private ClientePFService clientePFService;

    private MockedStatic<CPFUtil> cpfUtilMock;
    private MockedStatic<CEPUtil> cepUtilMock;

    @BeforeEach
    void setUp() {
        cpfUtilMock = mockStatic(CPFUtil.class);
        cepUtilMock = mockStatic(CEPUtil.class);
    }

    @AfterEach
    void tearDown() {
        cpfUtilMock.close();
        cepUtilMock.close();
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

    private void stubDocumentoValidoENaoExistente(ClientePF cliente) {
        cpfUtilMock.when(() -> CPFUtil.isValid(cliente.getCpf())).thenReturn(true);
        when(clientePFRepository.existsByCpf(cliente.getCpf())).thenReturn(false);
    }

    private void stubCepValido(ClientePF cliente) {
        cepUtilMock.when(() -> CEPUtil.isValid(cliente.getCep())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.clean(cliente.getCep())).thenReturn(cliente.getCep());
    }

    // ---------------------------------------------------------------
    // cadastrarCliente
    // ---------------------------------------------------------------

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeNulo() {
        ClientePF cliente = clienteValido();
        cliente.setNome(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.NULO_BRANCO.getErrorMessage(), ex.getMessage());
        verifyNoInteractions(clientePFRepository, contaService);
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeBranco() {
        ClientePF cliente = clienteValido();
        cliente.setNome("   ");

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeMaiorQue100() {
        ClientePF cliente = clienteValido();
        cliente.setNome("A".repeat(101));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CARACTERES_ACIMA.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCpfNulo() {
        ClientePF cliente = clienteValido();
        cliente.setCpf(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CPF_NULO_BRANCO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCpfBranco() {
        ClientePF cliente = clienteValido();
        cliente.setCpf(" ");

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCpfInvalido() {
        ClientePF cliente = clienteValido();
        cpfUtilMock.when(() -> CPFUtil.isValid(cliente.getCpf())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CPF_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoClienteJaCadastrado() {
        ClientePF cliente = clienteValido();
        cpfUtilMock.when(() -> CPFUtil.isValid(cliente.getCpf())).thenReturn(true);
        when(clientePFRepository.existsByCpf(cliente.getCpf())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoDataNascimentoNula() {
        ClientePF cliente = clienteValido();
        cliente.setDataNascimento(null);
        stubDocumentoValidoENaoExistente(cliente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.DATA_NASCIMENTO_NULO_BRANCO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoLogradouroNulo() {
        ClientePF cliente = clienteValido();
        cliente.setLogradouro(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoEnderecoNulo() {
        ClientePF cliente = clienteValido();
        cliente.setEndereco(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNumeroNulo() {
        ClientePF cliente = clienteValido();
        cliente.setNumero(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoBairroNulo() {
        ClientePF cliente = clienteValido();
        cliente.setBairro(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCepNulo() {
        ClientePF cliente = clienteValido();
        cliente.setCep(null);
        stubDocumentoValidoENaoExistente(cliente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.NULO_BRANCO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCepInvalido() {
        ClientePF cliente = clienteValido();
        stubDocumentoValidoENaoExistente(cliente);
        cepUtilMock.when(() -> CEPUtil.isValid(cliente.getCep())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CEP_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCidadeNula() {
        ClientePF cliente = clienteValido();
        cliente.setCidade(null);
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoEstadoNulo() {
        ClientePF cliente = clienteValido();
        cliente.setEstado(null);
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCamposEnderecoMuitoLongos() {
        ClientePF cliente = clienteValido();
        cliente.setLogradouro("A".repeat(51));
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePFService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CARACTERES_ACIMA.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveCadastrarComSucesso() {
        ClientePF cliente = clienteValido();
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);
        when(clientePFRepository.save(cliente)).thenReturn(cliente);

        Conta conta = Conta.builder()
                .numeroConta("123456")
                .agencia("0001")
                .saldo(BigDecimal.ZERO)
                .cliente(cliente)
                .build();
        when(contaService.criarConta(cliente, BigDecimal.ZERO)).thenReturn(conta);

        ClientePFDTO dto = clientePFService.cadastrarCliente(cliente);

        assertNotNull(dto);
        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getCpf(), dto.getCpf());
        verify(clientePFRepository).save(cliente);
        verify(contaService).criarConta(cliente, BigDecimal.ZERO);
    }

    // ---------------------------------------------------------------
    // pesquisaClientePorDocumento
    // ---------------------------------------------------------------

    @Test
    void pesquisaClientePorDocumento_deveRetornarDto_quandoEncontrado() {
        ClientePF cliente = clienteValido();
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));

        ClientePFDTO dto = clientePFService.pesquisaClientePorDocumento("12345678900");

        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getCpf(), dto.getCpf());
    }

    @Test
    void pesquisaClientePorDocumento_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePFRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clientePFService.pesquisaClientePorDocumento("00000000000"));
        assertEquals(ErrorEnum.CPF_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // atualizarDadosCliente
    // ---------------------------------------------------------------

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoClienteNaoEncontrado() {
        ClientePF novosDados = clienteValido();
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientePFService.atualizarDadosCliente("12345678900", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoCpfInvalido() {
        ClientePF existente = clienteValido();
        ClientePF novosDados = clienteValido();
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(existente));
        cpfUtilMock.when(() -> CPFUtil.isValid(novosDados.getCpf())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> clientePFService.atualizarDadosCliente("12345678900", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoCepInvalido() {
        ClientePF existente = clienteValido();
        ClientePF novosDados = clienteValido();
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(existente));
        cpfUtilMock.when(() -> CPFUtil.isValid(novosDados.getCpf())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.isValid(novosDados.getCep())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> clientePFService.atualizarDadosCliente("12345678900", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveAtualizarComSucesso() {
        ClientePF existente = clienteValido();
        ClientePF novosDados = clienteValido();
        novosDados.setNome("Novo Nome");

        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(existente));
        cpfUtilMock.when(() -> CPFUtil.isValid(novosDados.getCpf())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.isValid(novosDados.getCep())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.clean(novosDados.getCep())).thenReturn(novosDados.getCep());
        when(clientePFRepository.save(existente)).thenReturn(existente);

        ClientePFDTO dto = clientePFService.atualizarDadosCliente("12345678900", novosDados);

        assertEquals("Novo Nome", dto.getNome());
        verify(clientePFRepository).save(existente);
    }

    // ---------------------------------------------------------------
    // softDeleteCliente
    // ---------------------------------------------------------------

    @Test
    void softDeleteCliente_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientePFService.softDeleteCliente("12345678900"));
    }

    @Test
    void softDeleteCliente_deveDesativarComSucesso() {
        ClientePF cliente = clienteValido();
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));
        when(clientePFRepository.save(cliente)).thenReturn(cliente);

        ClientePFDTO dto = clientePFService.softDeleteCliente("12345678900");

        assertFalse(dto.isStatus());
        verify(clientePFRepository).save(cliente);
    }

    // ---------------------------------------------------------------
    // reativarCliente
    // ---------------------------------------------------------------

    @Test
    void reativarCliente_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientePFService.reativarCliente("12345678900"));
    }

    @Test
    void reativarCliente_deveReativar_quandoInativo() {
        ClientePF cliente = clienteValido();
        cliente.setStatus(false);
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));
        when(clientePFRepository.save(cliente)).thenReturn(cliente);

        ClientePFDTO dto = clientePFService.reativarCliente("12345678900");

        assertTrue(dto.isStatus());
    }

    @Test
    void reativarCliente_deveManterAtivo_quandoJaEstavaAtivo() {
        ClientePF cliente = clienteValido();
        cliente.setStatus(true);
        when(clientePFRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));
        when(clientePFRepository.save(cliente)).thenReturn(cliente);

        ClientePFDTO dto = clientePFService.reativarCliente("12345678900");

        assertTrue(dto.isStatus());
    }
}
