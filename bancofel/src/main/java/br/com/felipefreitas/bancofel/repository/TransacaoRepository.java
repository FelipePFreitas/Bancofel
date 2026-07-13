package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Transacao;
import br.com.felipefreitas.bancofel.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao,Long> {

    List<Transacao> findByTipoTransacao (TipoTransacao tipoTransacao);
}
