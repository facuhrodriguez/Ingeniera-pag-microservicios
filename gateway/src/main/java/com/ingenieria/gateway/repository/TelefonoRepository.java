package com.ingenieria.gateway.repository;

import com.ingenieria.gateway.domain.Telefono;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Telefono entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelefonoRepository extends ReactiveCrudRepository<Telefono, Long>, TelefonoRepositoryInternal {
    @Query("SELECT * FROM telefono entity WHERE entity.cliente_id = :id")
    Flux<Telefono> findByCliente(Long id);

    @Query("SELECT * FROM telefono entity WHERE entity.cliente_id IS NULL")
    Flux<Telefono> findAllWhereClienteIsNull();

    @Override
    <S extends Telefono> Mono<S> save(S entity);

    @Override
    Flux<Telefono> findAll();

    @Override
    Mono<Telefono> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TelefonoRepositoryInternal {
    <S extends Telefono> Mono<S> save(S entity);

    Flux<Telefono> findAllBy(Pageable pageable);

    Flux<Telefono> findAll();

    Mono<Telefono> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Telefono> findAllBy(Pageable pageable, Criteria criteria);

}
