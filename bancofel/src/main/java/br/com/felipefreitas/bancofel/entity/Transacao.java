package br.com.felipefreitas.bancofel.entity;

import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipoTransacao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime dataHoraTransacao;

    @ManyToOne
    @JoinColumn(name = "fk_conta_origem_conta", nullable = false)
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "fk_conta_destino_conta")
    private Conta contaDestino;
}