package br.com.felipefreitas.bancofel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "contas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false,length = 20)
    private String numeroConta;

    @Column(nullable = false,length = 20)
    private String agencia;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "fk_cliente_id",nullable = false)
    private Cliente cliente;
}
