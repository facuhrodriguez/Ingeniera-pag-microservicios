package com.ingenieria.gateway.repository;

import com.ingenieria.gateway.domain.DetalleFactura;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DetalleFactura entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DetalleFacturaRepository extends ReactiveCrudRepository<DetalleFactura, Long>, DetalleFacturaRepositoryInternal {
    @Query("SELECT * FROM detalle_factura entity WHERE entity.factura_id = :id")
    Flux<DetalleFactura> findByFactura(Long id);

    @Query("SELECT * FROM detalle_factura entity WHERE entity.factura_id IS NULL")
    Flux<DetalleFactura> findAllWhereFacturaIsNull();

    @Override
    <S extends DetalleFactura> Mono<S> save(S entity);

    @Override
    Flux<DetalleFactura> findAll();

    @Override
    Mono<DetalleFactura> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DetalleFacturaRepositoryInternal {
    <S extends DetalleFactura> Mono<S> save(S entity);

    Flux<DetalleFactura> findAllBy(Pageable pageable);

    Flux<DetalleFactura> findAll();

    Mono<DetalleFactura> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<DetalleFactura> findAllBy(Pageable pageable, Criteria criteria);

}
