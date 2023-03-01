package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.Cliente;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends ReactiveMongoRepository<Cliente, String> {

    Flux<Cliente> findByNombreRegexAndApellidoRegex(String nombreRegex, String apellidoRegex);

    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'telefono', localField: '_id', foreignField: 'cliente_id', as: 'telefono' } }",
        "{ $project: { '_id': 1, 'nombre': 1, 'apellido': 1, 'direccion': 1, 'activo': 1, 'telefono': { $cond: { if: { $eq: [ '$telefono', [] ] }, then: null, else: { $arrayElemAt: [ '$telefono', 0 ] }}} }}"
    })
    Mono<Cliente> findClienteByIdWithTelefono(String id);


}
