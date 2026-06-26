package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta,Long> {
}
