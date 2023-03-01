package com.ingenieria.factura.repository;

import com.ingenieria.factura.domain.Factura;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Factura entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturaRepository extends ReactiveCrudRepository<Factura, Long>, FacturaRepositoryInternal {
    @Override
    <S extends Factura> Mono<S> save(S entity);

    @Override
    Flux<Factura> findAll();

    @Override
    Mono<Factura> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    Flux<Factura> findAllByIdCliente(String idCliente);

    @Transactional(readOnly = true)
    @Query(
        "SELECT COALESCE(SUM(f.total_con_iva), 0) as gastoTotal " +
            "FROM factura f " +
            "WHERE f.id_cliente = :idCliente"
    )
    Mono<Double> getGastoTotalConIva(@Param("idCliente") String idCliente);

    @Transactional(readOnly = true)
    @Query(
        "SELECT DISTINCT f.* " +
            "FROM detalle_factura df " +
            "JOIN factura f ON (df.factura_id = f.id AND df.id_producto = :idProducto)"
    )
    Flux<Factura> getAllFacturas(@Param("idProducto") Long idProducto);

    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT f.id_cliente " + "FROM Factura f")
    Flux<String> getAllIdClients();
}

interface FacturaRepositoryInternal {
    <S extends Factura> Mono<S> save(S entity);

    Flux<Factura> findAllBy(Pageable pageable);

    Flux<Factura> findAll();

    Mono<Factura> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Factura> findAllBy(Pageable pageable, Criteria criteria);

}
