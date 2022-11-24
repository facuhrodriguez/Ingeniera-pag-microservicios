package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.Cliente;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends ReactiveMongoRepository<Cliente, String> {}
