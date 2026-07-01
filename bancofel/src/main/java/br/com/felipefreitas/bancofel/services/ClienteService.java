package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Cliente;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ClienteDTO;
import br.com.felipefreitas.bancofel.repository.ClienteRepository;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import br.com.felipefreitas.bancofel.utils.CEPUtil;
import br.com.felipefreitas.bancofel.utils.CPFUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void cadastrarCliente(Cliente cliente) {
        log.info("Iniciando cadastro do cliente com CPF: {}", cliente.getCpf());
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getSobrenome() == null || cliente.getSobrenome().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getNome().length() > 100 || cliente.getSobrenome().length() > 100) {
            throw new RuntimeException(ErrorEnum.CARACTERES_ACIMA.getErrorMessage());
        }

        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new RuntimeException(ErrorEnum.CPF_NULO_BRANCO.getErrorMessage());
        }

        if (!CPFUtil.isValid(cliente.getCpf())) {
            throw new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage());
        }

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RuntimeException(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage());
        }

        if (cliente.getDataNascimento() == null) {
            throw new RuntimeException(ErrorEnum.DATA_NASCIMENTO_NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getLogradouro() == null || cliente.getLogradouro().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getEndereco() == null || cliente.getEndereco().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getNumero() == null || cliente.getNumero().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getBairro() == null || cliente.getBairro().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getCep() == null || cliente.getCep().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (!CEPUtil.isValid(cliente.getCep())) {
            throw new RuntimeException(ErrorEnum.CEP_INVALIDO.getErrorMessage());
        }

        //Limpa pontuações para salvar o CEP padronizado apenas com números
        cliente.setCep(CEPUtil.clean(cliente.getCep()));

        if (cliente.getCidade() == null || cliente.getCidade().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getEstado() == null || cliente.getEstado().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getLogradouro().length() > 50 || cliente.getEndereco().length() > 50
                || cliente.getNumero().length() > 50 || cliente.getBairro().length() > 50
                || cliente.getCidade().length() > 50 || cliente.getEstado().length() > 50) {
            throw new RuntimeException(ErrorEnum.CARACTERES_ACIMA.getErrorMessage());
        }
        Cliente clienteSalvo = clienteRepository.save(cliente);

        String numeroConta;
        boolean contaJaExiste;

        do {
            // 1. Gera um número aleatório (ex: "482934")
            numeroConta = gerarNumeroConta();

            // 2. Vai no banco de dados e checa se já existe alguma conta com esse número
            contaJaExiste = contaRepository.existsByNumeroConta(numeroConta);

            if (contaJaExiste) {
                log.warn("O número de conta {} gerado já existe no banco. Tentando gerar outro...", numeroConta);
            }

        } while (contaJaExiste); // 3. Se existir, o loop repete, gera outro número e testa de novo.

        Conta novaConta = new Conta(numeroConta, "0001", BigDecimal.ZERO, clienteSalvo);

        contaRepository.save(novaConta);


        log.info("Cliente com CPF: {} cadastrado com sucesso! Conta bancária gerada: {}", clienteSalvo.getCpf(), numeroConta);
    }

    public String gerarNumeroConta() {
        // Gera um número aleatório entre 0 e 999999
        int numero = ThreadLocalRandom.current().nextInt(0, 10000000);

        // Converte o int diretamente para String
        return String.format("%04d", numero);
    }

    @Transactional(readOnly = true)
    public ClienteDTO pesquisaClienteCpf(String cpf) {

        Cliente cliente =
                clienteRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setNome(cliente.getNome());
        clienteDTO.setSobrenome(cliente.getSobrenome());
        clienteDTO.setCpf(cliente.getCpf());
        clienteDTO.setDataNascimento(LocalDate.parse(cliente.getDataNascimento().format(formatter)));
        clienteDTO.setLogradouro(cliente.getLogradouro());
        clienteDTO.setEndereco(cliente.getEndereco());
        clienteDTO.setNumero(cliente.getNumero());
        clienteDTO.setBairro(cliente.getBairro());
        clienteDTO.setCep(cliente.getCep());
        clienteDTO.setCidade(cliente.getCidade());
        clienteDTO.setEstado(cliente.getEstado());
        clienteDTO.setStatus(cliente.isStatus());

        return clienteDTO;
    }

    @Transactional
    public Cliente atualizarDadosCliente(String cpf, Cliente cliente) {
        log.info("Iniciando a atualização dos dados do cliente com CPF: {}", cpf);

        Cliente clienteExistente =
                clienteRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));


        if (!CPFUtil.isValid(cliente.getCpf())) {
            throw new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage());
        }

        if (!CEPUtil.isValid(cliente.getCep())) {
            throw new RuntimeException(ErrorEnum.CEP_INVALIDO.getErrorMessage());
        }

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setSobrenome(cliente.getSobrenome());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(LocalDate.parse(cliente.getDataNascimento().format(formatter)));
        clienteExistente.setLogradouro(cliente.getLogradouro());
        clienteExistente.setEndereco(cliente.getEndereco());
        clienteExistente.setNumero(cliente.getNumero());
        clienteExistente.setBairro(cliente.getBairro());
        clienteExistente.setCep(CEPUtil.clean(cliente.getCep()));
        clienteExistente.setCidade(cliente.getCidade());
        clienteExistente.setEstado(cliente.getEstado());

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);

        return clienteAtualizado;
    }

    @Transactional
    public void softDeleteCliente(String cpf) {
        Cliente clienteExistente =
                clienteRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        clienteExistente.setStatus(false);
        clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void reativarCliente(String cpf) {
        Cliente clienteExistente =
                clienteRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        if (!clienteExistente.isStatus()) {
            clienteExistente.setStatus(true);
        }

    }


}