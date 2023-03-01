package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.Telefono;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Telefono entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelefonoRepository extends ReactiveMongoRepository<Telefono, String> {

    Mono<Boolean> existsByClienteId(String clienteId);

    Flux<Telefono> findByCliente_Id(String clienteId);
}
