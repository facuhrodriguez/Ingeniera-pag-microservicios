package com.ingenieria.factura.repository;

import com.ingenieria.factura.domain.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Producto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductoRepository extends ReactiveCrudRepository<Producto, Long>, ProductoRepositoryInternal {
    @Override
    <S extends Producto> Mono<S> save(S entity);

    @Override
    Flux<Producto> findAll();

    @Override
    Mono<Producto> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductoRepositoryInternal {
    <S extends Producto> Mono<S> save(S entity);

    Flux<Producto> findAllBy(Pageable pageable);

    Flux<Producto> findAll();

    Mono<Producto> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Producto> findAllBy(Pageable pageable, Criteria criteria);

}
