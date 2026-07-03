package br.com.felipefreitas.bancofel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroConta;

    @Column(nullable = false, length = 20)
    private String agencia;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "fk_cliente_id", nullable = false)
    private Cliente cliente;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "conta_chaves_pix",
            joinColumns = @JoinColumn(name = "conta_id") // Aponta para a tabela de contas
    )
    @Column(name = "chave_pix", length = 77)
    @Builder.Default
    private Set<String> chavesPix = new HashSet<>();

}
