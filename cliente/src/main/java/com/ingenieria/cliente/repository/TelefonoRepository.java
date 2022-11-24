package com.ingenieria.cliente.repository;

import com.ingenieria.cliente.domain.Telefono;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Telefono entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelefonoRepository extends ReactiveMongoRepository<Telefono, String> {}
