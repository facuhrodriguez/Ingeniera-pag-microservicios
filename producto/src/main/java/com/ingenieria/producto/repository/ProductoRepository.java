package com.ingenieria.producto.repository;

import com.ingenieria.producto.domain.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    @Query("SELECT p.id FROM Producto p WHERE p.marca = :marca")
    Flux<Long> getAllIdByMarca(String marca);
}

interface ProductoRepositoryInternal {
    <S extends Producto> Mono<S> save(S entity);

    Flux<Producto> findAllBy(Pageable pageable);

    Flux<Producto> findAll();

    Mono<Producto> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Producto> findAllBy(Pageable pageable, Criteria criteria);

}
