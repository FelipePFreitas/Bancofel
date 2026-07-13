package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.entity.Transacao;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import br.com.felipefreitas.bancofel.models.TransacaoDTO;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import br.com.felipefreitas.bancofel.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor

public class TransacaoService {
    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;


    @Transactional
    public BigDecimal saque(String numeroConta, BigDecimal valorSaque) {
        Conta conta =
                contaRepository.findByNumeroConta(numeroConta).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        if (valorSaque == null || valorSaque.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(ErrorEnum.SAQUE_NULO_ZERO.getErrorMessage());
        }

        if (valorSaque.compareTo(conta.getSaldo()) > 0) {
            throw new RuntimeException(ErrorEnum.SAQUE_VALOR_MAIOR_SALDO.getErrorMessage());
        }
        BigDecimal saldoAtualizado = conta.getSaldo().subtract(valorSaque);
        conta.setSaldo(saldoAtualizado);
        contaRepository.save(conta);

        Transacao transacaoSaque = Transacao.builder()
                .tipoTransacao(TipoTransacao.SAQUE)
                .valor(valorSaque)
                .contaOrigem(conta)
                .build();

        transacaoRepository.save(transacaoSaque);

        return saldoAtualizado;
    }

    @Transactional
    public BigDecimal deposito(String numeroConta, BigDecimal valorDeposito) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        if (valorDeposito == null || valorDeposito.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(ErrorEnum.DEPOSITO_NULO_ZERO.getErrorMessage());
        }

        BigDecimal saldoAtualizado = conta.getSaldo().add(valorDeposito);

        conta.setSaldo(saldoAtualizado);

        contaRepository.save(conta);

        Transacao transacaoDeposito =
                Transacao.builder()
                        .tipoTransacao(TipoTransacao.DEPOSITO)
                        .valor(valorDeposito)
                        .contaOrigem(conta)
                        .build();

        transacaoRepository.save(transacaoDeposito);

        return saldoAtualizado;
    }

    @Transactional
    public TransacaoDTO transferencia(String contaOrigem, String contaDestino, BigDecimal valorTransferencia) {

        Conta conta1 =
                contaRepository.findByNumeroConta(contaOrigem).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        Conta conta2 =
                contaRepository.findByNumeroConta(contaDestino).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
        }


        if (conta1.getSaldo().compareTo(valorTransferencia) < 0) {
            throw new RuntimeException(ErrorEnum.SALDO_INSUFICIENTE.getErrorMessage());
        } else {
            BigDecimal valorConta1 = conta1.getSaldo().subtract(valorTransferencia);
            conta1.setSaldo(valorConta1);
            BigDecimal valorConta2 = conta2.getSaldo().add(valorTransferencia);
            conta2.setSaldo(valorConta2);
        }

        contaRepository.save(conta1);
        contaRepository.save(conta2);

        Transacao transacaoTransferencia = Transacao.builder()
                .tipoTransacao(TipoTransacao.TRANSFERENCIA)
                .valor(valorTransferencia)
                .contaOrigem(conta1)
                .contaDestino(conta2)
                .build();

        log.info("A transferência de R$ {} realizado com sucesso da conta {} para a conta {}", valorTransferencia,
                contaOrigem, contaDestino);

        transacaoRepository.save(transacaoTransferencia);

        return TransacaoDTO.builder()
                .tipoTransacao(transacaoTransferencia.getTipoTransacao())
                .valor(transacaoTransferencia.getValor())
                .contaOrigem(conta1.getNumeroConta())
                .contaDestino(conta2.getNumeroConta())
                .dataHoraTransacao(transacaoTransferencia.getDataHoraTransacao())
                .build();

    }

    @Transactional
    public TransacaoDTO pix(String contaOrigem, String chavePix, BigDecimal valorTransferencia) {

        Conta conta1 =
                contaRepository.findByNumeroConta(contaOrigem).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        Conta conta2 =
                contaRepository.findByChavePix(chavePix).orElseThrow(() -> new RuntimeException(ErrorEnum.CHAVEPIX_INEXISTENTE.getErrorMessage()));

        if (conta1.getNumeroConta().equals(conta2.getNumeroConta())) {
            throw new RuntimeException("Não é possível realizar um Pix para a sua própria conta.");
        }

        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
        }


        if (conta1.getSaldo().compareTo(valorTransferencia) < 0) {
            throw new RuntimeException(ErrorEnum.SALDO_INSUFICIENTE.getErrorMessage());
        } else {
            BigDecimal valorConta1 = conta1.getSaldo().subtract(valorTransferencia);
            conta1.setSaldo(valorConta1);
            BigDecimal valorConta2 = conta2.getSaldo().add(valorTransferencia);
            conta2.setSaldo(valorConta2);
        }

        contaRepository.save(conta1);
        contaRepository.save(conta2);

        Transacao transacaoTransferencia = Transacao.builder()
                .tipoTransacao(TipoTransacao.PIX)
                .valor(valorTransferencia)
                .contaOrigem(conta1)
                .contaDestino(conta2)
                .build();

        log.info("Pix de R$ {} realizado com sucesso da conta {} para a chave {}", valorTransferencia, contaOrigem, chavePix);

        Transacao transacaoPixSalva = transacaoRepository.save(transacaoTransferencia);

        return TransacaoDTO.builder()
                .tipoTransacao(transacaoPixSalva.getTipoTransacao())
                .valor(transacaoPixSalva.getValor())
                .contaOrigem(conta1.getNumeroConta())
                .contaDestino(conta2.getNumeroConta())
                .dataHoraTransacao(transacaoPixSalva.getDataHoraTransacao())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Transacao> listarTransacaoPorTipo(TipoTransacao tipoTransacao) {

        if (tipoTransacao == null) {
            throw new RuntimeException(ErrorEnum.TIPO_TRANSACAO_INEXISTENTE.getErrorMessage());
        }

        return transacaoRepository.findByTipoTransacao(tipoTransacao);
    }
}
