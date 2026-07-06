package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Cliente;
import br.com.felipefreitas.bancofel.entity.ClientePJ;
import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.models.ContaDTO;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public Conta criarConta(Cliente cliente, BigDecimal saldoInicial) {

        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
        }

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

        Conta novaConta =
                Conta.builder()
                        .numeroConta(numeroConta)
                        .agencia("0001")
                        .saldo(BigDecimal.ZERO)
                        .cliente(cliente)
                        .build();

        return contaRepository.save(novaConta);
    }

    public String gerarNumeroConta() {
        // Gera um número aleatório entre 0 e 999999
        int numero = ThreadLocalRandom.current().nextInt(0, 10000000);

        // Converte o int diretamente para String
        return String.format("%06d", numero);
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(String numeroConta) {

        Conta conta =
                contaRepository.findByNumeroConta(numeroConta).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        ContaDTO contaDTO =
                ContaDTO.builder()
                        .numeroConta(conta.getNumeroConta())
                        .agencia(conta.getAgencia())
                        .saldo(conta.getSaldo())
                        .idCliente(conta.getCliente().getId())
                        .build();

        return contaDTO.getSaldo();
    }

    @Transactional
    public void cadastrarChavePix(String numeroConta, String novaChave) {

        if (novaChave == null || novaChave.isBlank()) {
            throw new RuntimeException(ErrorEnum.NULO_BRANCO.getErrorMessage());
        }

        if (novaChave.length() > 77) {
            throw new RuntimeException(ErrorEnum.CARACTERES_ACIMA.getErrorMessage());
        }
        Conta conta =
                contaRepository.findByNumeroConta(numeroConta).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));


        int limitechaves = 5;

        if (conta.getCliente() instanceof ClientePJ) {
            limitechaves = 20;
        }


        if (conta.getChavesPix().size() >= limitechaves) {
            throw new RuntimeException(ErrorEnum.LIMITE_CHAVEPIX.getErrorMessage());
        }

        if (contaRepository.findByChavePix(novaChave).isPresent()) {
            throw new RuntimeException("Esta chave Pix já pertence a outra conta.");
        }

        if (!conta.getChavesPix().add(novaChave)) {
            throw new RuntimeException(ErrorEnum.CHAVEPIX_JACADASTRADA.getErrorMessage());
        }

        contaRepository.save(conta);
        log.info("Chave Pix cadastrada com sucesso para a conta {}", numeroConta);
    }
}