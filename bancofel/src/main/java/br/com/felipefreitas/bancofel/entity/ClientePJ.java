package br.com.felipefreitas.bancofel.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cliente_pj")
@PrimaryKeyJoinColumn(name = "cliente_id_pj") // Une a chave primária com a tabela mãe
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientePJ extends Cliente {

    @Column(unique = true, nullable = false, length = 20)
    private String cnpj;

    @Column(unique = true, nullable = false, length = 20)
    private String inscricaoEstadual;

}
