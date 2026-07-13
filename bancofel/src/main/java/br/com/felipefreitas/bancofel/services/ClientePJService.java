package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ClienteTipo;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.interfaces.ClienteImpl;
import br.com.felipefreitas.bancofel.models.ClientePJDTO;
import br.com.felipefreitas.bancofel.repository.ClientePJRepository;
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
public class ClientePJService implements ClienteImpl<ClientePJ, ClientePJDTO> {

    private final ContaService contaService;
    private final ClientePJRepository clientePJRepository;

    @Override
    @Transactional
    public ClientePJDTO cadastrarCliente(ClientePJ cliente) {
        log.info("Iniciando cadastro do cliente com CNPJ: {}", cliente.getCnpj());

        cliente.setClienteTipo(ClienteTipo.PESSOA_JURIDICA);

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

        if (clientePJRepository.existsByCnpj(cliente.getCnpj())) {
            throw new RuntimeException(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage());
        }

        if (cliente.getInscricaoEstadual() == null) {
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
        ClientePJ clienteSalvo = clientePJRepository.save(cliente);

        ClientePJDTO clientePJDTO = ClientePJDTO.builder()
                .nome(clienteSalvo.getNome())
                .cnpj(clienteSalvo.getCnpj())
                .inscricaoEstadual(clienteSalvo.getInscricaoEstadual())
                .logradouro(clienteSalvo.getLogradouro())
                .endereco(clienteSalvo.getEndereco())
                .numero(clienteSalvo.getNumero())
                .cep(clienteSalvo.getCep())
                .bairro(clienteSalvo.getBairro())
                .cidade(clienteSalvo.getCidade())
                .estado(clienteSalvo.getEstado())
                .status(clienteSalvo.isStatus())
                .build();


        Conta novaConta = contaService.criarConta(clienteSalvo, BigDecimal.ZERO);


        log.info("Cliente com CNPJ: {} cadastrado com sucesso! Conta bancária gerada: {}", clienteSalvo.getCnpj(),
                novaConta.getNumeroConta());

        return clientePJDTO;
    }


    @Override
    @Transactional(readOnly = true)
    public ClientePJDTO pesquisaClientePorDocumento(String documento) {

        ClientePJ clientePJ =
                clientePJRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        return ClientePJDTO.builder()
                .nome(clientePJ.getNome())
                .cnpj(clientePJ.getCnpj())
                .inscricaoEstadual(clientePJ.getInscricaoEstadual())
                .logradouro(clientePJ.getLogradouro())
                .endereco(clientePJ.getEndereco())
                .numero(clientePJ.getNumero())
                .cep(clientePJ.getCep())
                .bairro(clientePJ.getBairro())
                .cidade(clientePJ.getCidade())
                .estado(clientePJ.getEstado())
                .status(clientePJ.isStatus())
                .build();

    }

    @Override
    @Transactional
    public ClientePJDTO atualizarDadosCliente(String documento, ClientePJ cliente) {
        log.info("Iniciando a atualização dos dados do cliente com CPF: {}", documento);

        ClientePJ clienteExistente =
                clientePJRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));


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

        ClientePJ clienteAtualizado = clientePJRepository.save(clienteExistente);

        log.info("Dados do cliente com CNPJ: {} atualizados com sucesso.", documento);

        return ClientePJDTO.builder()
                .nome(clienteAtualizado.getNome())
                .cnpj(clienteAtualizado.getCnpj())
                .inscricaoEstadual(clienteAtualizado.getInscricaoEstadual())
                .logradouro(clienteAtualizado.getLogradouro())
                .endereco(clienteAtualizado.getEndereco())
                .numero(clienteAtualizado.getNumero())
                .cep(clienteAtualizado.getCep())
                .bairro(clienteAtualizado.getBairro())
                .cidade(clienteAtualizado.getCidade())
                .estado(clienteAtualizado.getEstado())
                .status(clienteAtualizado.isStatus())
                .build();

    }

    @Override
    @Transactional
    public ClientePJDTO softDeleteCliente(String documento) {
        ClientePJ clienteExistente =
                clientePJRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        clienteExistente.setStatus(false);

        clientePJRepository.save(clienteExistente);

        log.info("Cliente com CNPJ: {} foi desativado com sucesso (status = false).", documento);

        return ClientePJDTO.builder()
                .nome(clienteExistente.getNome())
                .cnpj(clienteExistente.getCnpj())
                .inscricaoEstadual(clienteExistente.getInscricaoEstadual())
                .logradouro(clienteExistente.getLogradouro())
                .endereco(clienteExistente.getEndereco())
                .numero(clienteExistente.getNumero())
                .cep(clienteExistente.getCep())
                .bairro(clienteExistente.getBairro())
                .cidade(clienteExistente.getCidade())
                .estado(clienteExistente.getEstado())
                .status(clienteExistente.isStatus())
                .build();
    }

    @Override
    @Transactional
    public ClientePJDTO reativarCliente(String documento) {
        ClientePJ clienteExistente =
                clientePJRepository.findByCnpj(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CNPJ_INVALIDO.getErrorMessage()));

        if (!clienteExistente.isStatus()) {
            clienteExistente.setStatus(true);
        }

        clientePJRepository.save(clienteExistente);

        log.info("Cliente com CNPJ: {} foi reativado com sucesso (status = true).", documento);

        return ClientePJDTO.builder()
                .nome(clienteExistente.getNome())
                .cnpj(clienteExistente.getCnpj())
                .inscricaoEstadual(clienteExistente.getInscricaoEstadual())
                .logradouro(clienteExistente.getLogradouro())
                .endereco(clienteExistente.getEndereco())
                .numero(clienteExistente.getNumero())
                .cep(clienteExistente.getCep())
                .bairro(clienteExistente.getBairro())
                .cidade(clienteExistente.getCidade())
                .estado(clienteExistente.getEstado())
                .status(clienteExistente.isStatus())
                .build();
    }
}