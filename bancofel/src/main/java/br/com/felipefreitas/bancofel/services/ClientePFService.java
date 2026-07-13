package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.ClientePF;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ClienteTipo;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.interfaces.ClienteImpl;
import br.com.felipefreitas.bancofel.models.ClientePFDTO;
import br.com.felipefreitas.bancofel.repository.ClientePFRepository;
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
public class ClientePFService implements ClienteImpl<ClientePF, ClientePFDTO> {

    private final ContaService contaService;
    private final ClientePFRepository clientePFRepository;

    @Override
    @Transactional
    public ClientePFDTO cadastrarCliente(ClientePF cliente) {
        log.info("Iniciando cadastro do cliente com CPF: {}", cliente.getCpf());

        cliente.setClienteTipo(ClienteTipo.PESSOA_FISICA);

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

        if (clientePFRepository.existsByCpf(cliente.getCpf())) {
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
        ClientePF clienteSalvo = clientePFRepository.save(cliente);

        ClientePFDTO clientePFDTO = ClientePFDTO.builder()
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .dataNascimento(cliente.getDataNascimento())
                .logradouro(cliente.getLogradouro())
                .endereco(cliente.getEndereco())
                .numero(cliente.getNumero())
                .cep(cliente.getCep())
                .bairro(cliente.getBairro())
                .cidade(cliente.getCidade())
                .estado(cliente.getEstado())
                .status(cliente.isStatus())
                .build();

        Conta novaConta = contaService.criarConta(clienteSalvo, BigDecimal.ZERO);


        log.info("Cliente com CPF: {} cadastrado com sucesso! Conta bancária gerada: {}", clienteSalvo.getCpf(),
                novaConta.getNumeroConta());

        return clientePFDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePFDTO pesquisaClientePorDocumento(String documento) {

        ClientePF clientePF =
                clientePFRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        return ClientePFDTO.builder()
                .nome(clientePF.getNome())
                .cpf(clientePF.getCpf())
                .dataNascimento(clientePF.getDataNascimento())
                .logradouro(clientePF.getLogradouro())
                .endereco(clientePF.getEndereco())
                .numero(clientePF.getNumero())
                .cep(clientePF.getCep())
                .bairro(clientePF.getBairro())
                .cidade(clientePF.getCidade())
                .estado(clientePF.getEstado())
                .status(clientePF.isStatus())
                .build();
    }

    @Override
    @Transactional
    public ClientePFDTO atualizarDadosCliente(String documento, ClientePF cliente) {
        log.info("Iniciando a atualização dos dados do cliente com CPF: {}", documento);

        ClientePF clienteExistente =
                clientePFRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));


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

        ClientePF clienteAtualizado = clientePFRepository.save(clienteExistente);

        ClientePFDTO clientePFDTO = ClientePFDTO.builder()
                .nome(clienteAtualizado.getNome())
                .cpf(clienteAtualizado.getCpf())
                .dataNascimento(clienteAtualizado.getDataNascimento())
                .logradouro(clienteAtualizado.getLogradouro())
                .endereco(clienteAtualizado.getEndereco())
                .numero(clienteAtualizado.getNumero())
                .cep(clienteAtualizado.getCep())
                .bairro(clienteAtualizado.getBairro())
                .cidade(clienteAtualizado.getCidade())
                .estado(clienteAtualizado.getEstado())
                .status(clienteAtualizado.isStatus())
                .build();

        log.info("Dados do cliente com CPF: {} atualizados com sucesso.", documento);

        return clientePFDTO;
    }

    @Override
    @Transactional
    public ClientePFDTO softDeleteCliente(String documento) {
        ClientePF clienteExistente =
                clientePFRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        clienteExistente.setStatus(false);

        clientePFRepository.save(clienteExistente);

        log.info("Cliente com CPF: {} foi desativado com sucesso (status = false).", documento);

        return ClientePFDTO.builder()
                .nome(clienteExistente.getNome())
                .cpf(clienteExistente.getCpf())
                .dataNascimento(clienteExistente.getDataNascimento())
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
    public ClientePFDTO reativarCliente(String documento) {
        ClientePF clienteExistente =
                clientePFRepository.findByCpf(documento).orElseThrow(() -> new RuntimeException(ErrorEnum.CPF_INVALIDO.getErrorMessage()));

        if (!clienteExistente.isStatus()) {
            clienteExistente.setStatus(true);
        }

        clientePFRepository.save(clienteExistente);

        log.info("Cliente com CPF: {} foi reativado com sucesso (status = true).", documento);

        return ClientePFDTO.builder()
                .nome(clienteExistente.getNome())
                .cpf(clienteExistente.getCpf())
                .dataNascimento(clienteExistente.getDataNascimento())
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
