package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.ClientePJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientePJRepository extends JpaRepository<ClientePJ, Long> {

    boolean existsByCnpj(String cnpj);

    Optional<ClientePJ> findByCnpj(String cnpj);
}
