package br.com.felipefreitas.bancofel.services;

import br.com.felipefreitas.bancofel.entity.Conta;
import br.com.felipefreitas.bancofel.entity.Transacao;
import br.com.felipefreitas.bancofel.enums.ErrorEnum;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import br.com.felipefreitas.bancofel.repository.ContaRepository;
import br.com.felipefreitas.bancofel.repository.TransacaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransacaoService {
    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public void saque(String numeroConta, BigDecimal valorSaque) {
        Conta conta =
                contaRepository.findBynumeroConta(numeroConta).orElseThrow(() -> new RuntimeException(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage()));

        if (valorSaque == null ||valorSaque.compareTo(BigDecimal.ZERO)<= 0){
            throw new RuntimeException(ErrorEnum.SAQUE_NULO_ZERO.getErrorMessage());
        }

        if (valorSaque.compareTo(conta.getSaldo()) > 0){
            throw new RuntimeException(ErrorEnum.SAQUE_VALOR_MAIOR_SALDO.getErrorMessage());
        }
        BigDecimal novoSaldo = conta.getSaldo().subtract(valorSaque);
        conta.setSaldo(novoSaldo);
        contaRepository.save(conta);

        Transacao transacao = Transacao.builder()
                .tipoTransacao(TipoTransacao.SAQUE)
                .valor(valorSaque)
                .contaOrigem(conta)
                .build();

        transacaoRepository.save(transacao);
    }

}
