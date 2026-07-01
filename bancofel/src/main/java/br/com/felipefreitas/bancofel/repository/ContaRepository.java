package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta,Long> {

    boolean existsByNumeroConta(String numeroConta);

    Optional<Conta> findBynumeroConta(String numeroConta);
}
