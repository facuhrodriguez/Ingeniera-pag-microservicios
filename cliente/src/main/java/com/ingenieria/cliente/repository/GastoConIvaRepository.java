package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.GastoTotalIva;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the GastoConIva entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GastoConIvaRepository extends ReactiveMongoRepository<GastoTotalIva, String> {
    Mono<GastoTotalIva> findGastoTotalIvaByCliente_Id(String clienteId);
}
