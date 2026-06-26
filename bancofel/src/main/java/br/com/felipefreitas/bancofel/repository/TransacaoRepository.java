package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao,Long> {
}
