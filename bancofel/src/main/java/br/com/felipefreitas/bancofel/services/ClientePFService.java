package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.interfaces.ClienteService;
import br.com.felipefreitas.bancofel.repository.ClienteRepository;
import br.com.felipefreitas.bancofel.utils.CEPUtil;
import br.com.felipefreitas.bancofel.utils.CPFUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class ClientePFService implements ClienteService<ClientePF> {

    private final ContaService contaService;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClientePF cadastrarCliente(ClientePF cliente) {
        log.info("Iniciando cadastro do cliente com CPF: {}", cliente.getCpf());
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getNome().length() > 100) {
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
        ClientePF clienteSalvo = clienteRepository.save(cliente);

        Conta novaConta = contaService.criarConta(clienteSalvo, BigDecimal.ZERO);


        log.info("Cliente com CPF: {} cadastrado com sucesso! Conta bancária gerada: {}", clienteSalvo.getCpf(),
                novaConta.getNumeroConta());

        return clienteSalvo;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePF pesquisaClientePorDocumento(String documento) {
        return clienteRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

    }

    @Override
    @Transactional
    public ClientePF atualizarDadosCliente(String documento, ClientePF cliente) {
        log.info("Iniciando a atualização dos dados do cliente com CPF: {}", documento);

        ClientePF clienteExistente =
                clienteRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));


        if (!CPFUtil.isValid(cliente.getCpf())) {
            throw new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage());
        }

        if (!CEPUtil.isValid(cliente.getCep())) {
            throw new RuntimeException(ErrorEnum.CEP_INVALIDO.getErrorMessage());
        }

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());
        clienteExistente.setLogradouro(cliente.getLogradouro());
        clienteExistente.setEndereco(cliente.getEndereco());
        clienteExistente.setNumero(cliente.getNumero());
        clienteExistente.setBairro(cliente.getBairro());
        clienteExistente.setCep(CEPUtil.clean(cliente.getCep()));
        clienteExistente.setCidade(cliente.getCidade());
        clienteExistente.setEstado(cliente.getEstado());

        ClientePF clienteAtualizado = clienteRepository.save(clienteExistente);

        log.info("Dados do cliente com CPF: {} atualizados com sucesso.", documento);

        return clienteAtualizado;
    }

    @Override
    @Transactional
    public ClientePF softDeleteCliente(String documento) {
        ClientePF clienteExistente =
                clienteRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        clienteExistente.setStatus(false);

        clienteRepository.save(clienteExistente);

        log.info("Cliente com CPF: {} foi desativado com sucesso (status = false).", documento);

        return clienteExistente;
    }

    @Override
    @Transactional
    public ClientePF reativarCliente(String documento) {
        ClientePF clienteExistente =
                clienteRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        if (!clienteExistente.isStatus()) {
            clienteExistente.setStatus(true);
        }

        clienteRepository.save(clienteExistente);

        log.info("Cliente com CPF: {} foi reativado com sucesso (status = true).", documento);

        return clienteExistente;
    }
}
