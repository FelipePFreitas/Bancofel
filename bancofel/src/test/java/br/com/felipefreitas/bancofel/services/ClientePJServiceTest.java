package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ClientePJDTO;
import br.com.felipefreitas.bancofel.repository.ClientePJRepository;
import br.com.felipefreitas.bancofel.utils.CEPUtil;
import br.com.felipefreitas.bancofel.utils.CNPJUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * OBS: estrutura de ClientePJ / ClientePJDTO / CNPJUtil inferida a partir do
 * uso em ClientePJService, já que essas classes não foram fornecidas.
 *
 * OBS 2: no método atualizarDadosCliente do serviço original, o orElseThrow
 * do findByCnpj lança ErrorEnum.CPF_INVALIDO (não CNPJ_INVALIDO) — isso está
 * refletido fielmente no teste correspondente, pois reflete o comportamento
 * real do código, ainda que pareça um possível bug a ser revisado.
 */
@ExtendWith(MockitoExtension.class)
class ClientePJServiceTest {

    @Mock
    private ContaService contaService;

    @Mock
    private ClientePJRepository clientePJRepository;

    @InjectMocks
    private ClientePJService clientePJService;

    private MockedStatic<CNPJUtil> cnpjUtilMock;
    private MockedStatic<CEPUtil> cepUtilMock;

    @BeforeEach
    void setUp() {
        cnpjUtilMock = mockStatic(CNPJUtil.class);
        cepUtilMock = mockStatic(CEPUtil.class);
    }

    @AfterEach
    void tearDown() {
        cnpjUtilMock.close();
        cepUtilMock.close();
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

    private void stubDocumentoValidoENaoExistente(ClientePJ cliente) {
        cnpjUtilMock.when(() -> CNPJUtil.isValid(cliente.getCnpj())).thenReturn(true);
        when(clientePJRepository.existsByCnpj(cliente.getCnpj())).thenReturn(false);
    }

    private void stubCepValido(ClientePJ cliente) {
        cepUtilMock.when(() -> CEPUtil.isValid(cliente.getCep())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.clean(cliente.getCep())).thenReturn(cliente.getCep());
    }

    // ---------------------------------------------------------------
    // cadastrarCliente
    // ---------------------------------------------------------------

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setNome(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.NULO_BRANCO.getErrorMessage(), ex.getMessage());
        verifyNoInteractions(clientePJRepository, contaService);
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeBranco() {
        ClientePJ cliente = clienteValido();
        cliente.setNome("  ");

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNomeMaiorQue100() {
        ClientePJ cliente = clienteValido();
        cliente.setNome("A".repeat(101));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CARACTERES_ACIMA.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCnpjNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setCnpj(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CNPJ_NULO_BRANCO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCnpjBranco() {
        ClientePJ cliente = clienteValido();
        cliente.setCnpj(" ");

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCnpjInvalido() {
        ClientePJ cliente = clienteValido();
        cnpjUtilMock.when(() -> CNPJUtil.isValid(cliente.getCnpj())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CNPJ_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoClienteJaCadastrado() {
        ClientePJ cliente = clienteValido();
        cnpjUtilMock.when(() -> CNPJUtil.isValid(cliente.getCnpj())).thenReturn(true);
        when(clientePJRepository.existsByCnpj(cliente.getCnpj())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoInscricaoEstadualNula() {
        ClientePJ cliente = clienteValido();
        cliente.setInscricaoEstadual(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoLogradouroNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setLogradouro(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoEnderecoNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setEndereco(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoNumeroNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setNumero(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoBairroNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setBairro(null);
        stubDocumentoValidoENaoExistente(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCepNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setCep(null);
        stubDocumentoValidoENaoExistente(cliente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.NULO_BRANCO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCepInvalido() {
        ClientePJ cliente = clienteValido();
        stubDocumentoValidoENaoExistente(cliente);
        cepUtilMock.when(() -> CEPUtil.isValid(cliente.getCep())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CEP_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCidadeNula() {
        ClientePJ cliente = clienteValido();
        cliente.setCidade(null);
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoEstadoNulo() {
        ClientePJ cliente = clienteValido();
        cliente.setEstado(null);
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
    }

    @Test
    void cadastrarCliente_deveLancarExcecao_quandoCamposEnderecoMuitoLongos() {
        ClientePJ cliente = clienteValido();
        cliente.setEndereco("A".repeat(51));
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clientePJService.cadastrarCliente(cliente));
        assertEquals(ErrorEnum.CARACTERES_ACIMA.getErrorMessage(), ex.getMessage());
    }

    @Test
    void cadastrarCliente_deveCadastrarComSucesso() {
        ClientePJ cliente = clienteValido();
        stubDocumentoValidoENaoExistente(cliente);
        stubCepValido(cliente);
        when(clientePJRepository.save(cliente)).thenReturn(cliente);

        Conta conta = Conta.builder()
                .numeroConta("654321")
                .agencia("0001")
                .saldo(BigDecimal.ZERO)
                .cliente(cliente)
                .build();
        when(contaService.criarConta(cliente, BigDecimal.ZERO)).thenReturn(conta);

        ClientePJDTO dto = clientePJService.cadastrarCliente(cliente);

        assertNotNull(dto);
        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getCnpj(), dto.getCnpj());
        verify(clientePJRepository).save(cliente);
        verify(contaService).criarConta(cliente, BigDecimal.ZERO);
    }

    // ---------------------------------------------------------------
    // pesquisaClientePorDocumento
    // ---------------------------------------------------------------

    @Test
    void pesquisaClientePorDocumento_deveRetornarDto_quandoEncontrado() {
        ClientePJ cliente = clienteValido();
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(cliente));

        ClientePJDTO dto = clientePJService.pesquisaClientePorDocumento("12345678000199");

        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getCnpj(), dto.getCnpj());
    }

    @Test
    void pesquisaClientePorDocumento_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePJRepository.findByCnpj("00000000000000")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clientePJService.pesquisaClientePorDocumento("00000000000000"));
        assertEquals(ErrorEnum.CNPJ_INVALIDO.getErrorMessage(), ex.getMessage());
    }

    // ---------------------------------------------------------------
    // atualizarDadosCliente
    // ---------------------------------------------------------------

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoClienteNaoEncontrado() {
        ClientePJ novosDados = clienteValido();
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> clientePJService.atualizarDadosCliente("12345678000199", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoCnpjInvalido() {
        ClientePJ existente = clienteValido();
        ClientePJ novosDados = clienteValido();
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(existente));
        cnpjUtilMock.when(() -> CNPJUtil.isValid(novosDados.getCnpj())).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> clientePJService.atualizarDadosCliente("12345678000199", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveLancarExcecao_quandoCepInvalido() {
        ClientePJ existente = clienteValido();
        ClientePJ novosDados = clienteValido();
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(existente));
        cnpjUtilMock.when(() -> CNPJUtil.isValid(novosDados.getCnpj())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.isValid(novosDados.getCep())).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> clientePJService.atualizarDadosCliente("12345678000199", novosDados));
    }

    @Test
    void atualizarDadosCliente_deveAtualizarComSucesso() {
        ClientePJ existente = clienteValido();
        ClientePJ novosDados = clienteValido();
        novosDados.setNome("Novo Nome LTDA");

        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(existente));
        cnpjUtilMock.when(() -> CNPJUtil.isValid(novosDados.getCnpj())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.isValid(novosDados.getCep())).thenReturn(true);
        cepUtilMock.when(() -> CEPUtil.clean(novosDados.getCep())).thenReturn(novosDados.getCep());
        when(clientePJRepository.save(existente)).thenReturn(existente);

        ClientePJDTO dto = clientePJService.atualizarDadosCliente("12345678000199", novosDados);

        assertEquals("Novo Nome LTDA", dto.getNome());
        verify(clientePJRepository).save(existente);
    }

    // ---------------------------------------------------------------
    // softDeleteCliente
    // ---------------------------------------------------------------

    @Test
    void softDeleteCliente_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientePJService.softDeleteCliente("12345678000199"));
    }

    @Test
    void softDeleteCliente_deveDesativarComSucesso() {
        ClientePJ cliente = clienteValido();
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(cliente));
        when(clientePJRepository.save(cliente)).thenReturn(cliente);

        ClientePJDTO dto = clientePJService.softDeleteCliente("12345678000199");

        assertFalse(dto.isStatus());
        verify(clientePJRepository).save(cliente);
    }

    // ---------------------------------------------------------------
    // reativarCliente
    // ---------------------------------------------------------------

    @Test
    void reativarCliente_deveLancarExcecao_quandoNaoEncontrado() {
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientePJService.reativarCliente("12345678000199"));
    }

    @Test
    void reativarCliente_deveReativar_quandoInativo() {
        ClientePJ cliente = clienteValido();
        cliente.setStatus(false);
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(cliente));
        when(clientePJRepository.save(cliente)).thenReturn(cliente);

        ClientePJDTO dto = clientePJService.reativarCliente("12345678000199");

        assertTrue(dto.isStatus());
    }

    @Test
    void reativarCliente_deveManterAtivo_quandoJaEstavaAtivo() {
        ClientePJ cliente = clienteValido();
        cliente.setStatus(true);
        when(clientePJRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(cliente));
        when(clientePJRepository.save(cliente)).thenReturn(cliente);

        ClientePJDTO dto = clientePJService.reativarCliente("12345678000199");

        assertTrue(dto.isStatus());
    }
}
