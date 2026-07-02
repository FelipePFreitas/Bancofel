package br.com.felipefreitas.bancofel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String logradouro;

    @Column(nullable = false, length = 50)
    private String endereco;

    @Column(nullable = false, length = 50)
    private String numero;

    @Column(nullable = false, length = 50)
    private String bairro;

    @Column(nullable = false, length = 50)
    private String cep;

    @Column(nullable = false, length = 50)
    private String cidade;

    @Column(nullable = false, length = 50)
    private String estado;

    @OneToMany(mappedBy = "cliente")
    private List<Conta> contas;

    @Column(nullable = false)
    private boolean status = true;
}
