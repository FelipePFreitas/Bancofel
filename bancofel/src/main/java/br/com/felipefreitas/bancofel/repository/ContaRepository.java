package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta,Long> {

    boolean existsByNumeroConta(String numeroConta);

    Optional<Conta> findByNumeroConta(String numeroConta);

    @Query("SELECT c FROM Conta c JOIN c.chavesPix cp WHERE cp = :chavePix")
    Optional<Conta> findByChavePix(@Param("chavePix") String chavesPix);
}

