package br.com.felipefreitas.bancofel.repository;

import br.com.felipefreitas.bancofel.entity.Cliente;
import br.com.felipefreitas.bancofel.entity.ClientePF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {

   boolean existsByCpf(String cpf);

   Optional<ClientePF> findByCpf(String cpf);
}
