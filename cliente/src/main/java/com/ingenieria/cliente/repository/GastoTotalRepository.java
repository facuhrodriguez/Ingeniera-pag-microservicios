package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.GastoTotal;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the GastoConIva entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GastoTotalRepository extends ReactiveMongoRepository<GastoTotal, String> {
    Mono<GastoTotal> findGastoTotalByCliente_Id(String clienteId);
}
