package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ClienteTipo;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.interfaces.ClienteImpl;
import br.com.felipefreitas.bancofel.repository.ClienteRepository;
import br.com.felipefreitas.bancofel.utils.CEPUtil;
import br.com.felipefreitas.bancofel.utils.CNPJUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class ClientePJService implements ClienteImpl<ClientePJ> {

    private final ContaService contaService;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClientePJ cadastrarCliente(ClientePJ cliente) {
        log.info("Iniciando cadastro do cliente com CNPJ: {}", cliente.getCnpj());

        if (cliente.getClienteTipo() != ClienteTipo.PESSOA_JURIDICA) {
            throw new RuntimeException(ErrorEnum.TIPO_CLIENTE_INVALIDO.getErrorMessage());
        }

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (cliente.getNome().length() > 100) {
            throw new RuntimeException(ErrorEnum.CARACTERES_ACIMA.getErrorMessage());
        }

        if (cliente.getCnpj() == null || cliente.getCnpj().isBlank()) {
            throw new RuntimeException(ErrorEnum.CNPJ_NULO_BRANCO.getErrorMessage());
        }

        if (!CNPJUtil.isValid(cliente.getCnpj())) {
            throw new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage());
        }

        if (clienteRepository.existsByCnpj(cliente.getCnpj())) {
            throw new RuntimeException(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage());
        }

        if (cliente.getInscricaoEstadual()== null) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
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
        ClientePJ clienteSalvo = clienteRepository.save(cliente);

        Conta novaConta = contaService.criarConta(clienteSalvo, BigDecimal.ZERO);


        log.info("Cliente com CNPJ: {} cadastrado com sucesso! Conta bancária gerada: {}", clienteSalvo.getCnpj(),
                novaConta.getNumeroConta());

        return clienteSalvo;
    }


    @Override
    @Transactional(readOnly = true)
    public ClientePJ pesquisaClientePorDocumento(String documento) {
        return clienteRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

    }

    @Override
    @Transactional
    public ClientePJ atualizarDadosCliente(String documento, ClientePJ cliente) {
        log.info("Iniciando a atualização dos dados do cliente com CPF: {}", documento);

        ClientePJ clienteExistente =
                clienteRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));


        if (!CNPJUtil.isValid(cliente.getCnpj())) {
            throw new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage());
        }

        if (!CEPUtil.isValid(cliente.getCep())) {
            throw new RuntimeException(ErrorEnum.CEP_INVALIDO.getErrorMessage());
        }

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setCnpj(cliente.getCnpj());
        clienteExistente.setLogradouro(cliente.getLogradouro());
        clienteExistente.setEndereco(cliente.getEndereco());
        clienteExistente.setNumero(cliente.getNumero());
        clienteExistente.setBairro(cliente.getBairro());
        clienteExistente.setCep(CEPUtil.clean(cliente.getCep()));
        clienteExistente.setCidade(cliente.getCidade());
        clienteExistente.setEstado(cliente.getEstado());

        ClientePJ clienteAtualizado = clienteRepository.save(clienteExistente);

        log.info("Dados do cliente com CNPJ: {} atualizados com sucesso.", documento);

        return clienteAtualizado;
    }

    @Override
    @Transactional
    public ClientePJ softDeleteCliente(String documento) {
        ClientePJ clienteExistente =
                clienteRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        clienteExistente.setStatus(false);

        clienteRepository.save(clienteExistente);

        log.info("Cliente com CNPJ: {} foi desativado com sucesso (status = false).", documento);

        return clienteExistente;
    }

    @Override
    @Transactional
    public ClientePJ reativarCliente(String documento) {
        ClientePJ clienteExistente =
                clienteRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        if (!clienteExistente.isStatus()) {
            clienteExistente.setStatus(true);
        }

        clienteRepository.save(clienteExistente);

        log.info("Cliente com CNPJ: {} foi reativado com sucesso (status = true).", documento);

        return clienteExistente;
    }
}

