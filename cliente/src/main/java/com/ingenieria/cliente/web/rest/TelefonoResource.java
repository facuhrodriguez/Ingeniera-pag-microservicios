package com.ingenieria.cliente.web.rest;

import com.ingenieria.cliente.domain.Telefono;
import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.repository.TelefonoRepository;
import com.ingenieria.cliente.service.TelefonoService;
import com.ingenieria.cliente.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing {@link com.ingenieria.cliente.domain.Telefono}.
 */
@RestController
@RequestMapping("/api")
public class TelefonoResource {

    private final Logger log = LoggerFactory.getLogger(TelefonoResource.class);

    private static final String ENTITY_NAME = "clienteTelefono";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TelefonoRepository telefonoRepository;
    private final ClienteRepository clienteRepository;
    private final TelefonoService telefonoService;

    public TelefonoResource(TelefonoRepository telefonoRepository, ClienteRepository clienteRepository, TelefonoService telefonoService) {
        this.telefonoRepository = telefonoRepository;
        this.clienteRepository = clienteRepository;
        this.telefonoService = telefonoService;
    }

    /**
     * {@code POST  /telefonos} : Create a new telefono.
     *
     * @param telefono the telefono to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telefono, or with status {@code 400 (Bad Request)} if the telefono has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/telefonos")
    public Mono<ResponseEntity<Telefono>> createTelefono(@RequestBody Telefono telefono, @RequestParam String client_id) throws URISyntaxException {
        log.debug("REST request to save Telefono : {}", telefono);
        if (telefono.getId() != null) {
            throw new BadRequestAlertException("A new telefono cannot already have an ID", ENTITY_NAME, "idexists");
        }

        return telefonoRepository.existsByClienteId(client_id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
            .flatMap((b) ->
                clienteRepository.findById(client_id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .flatMap(cliente -> {
                        telefono.setCliente(cliente);

                        return telefonoRepository
                            .save(telefono)
                            .map(result -> {
                                try {
                                    return ResponseEntity
                                        .created(new URI("/api/telefonos/" + result.getId()))
                                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                                        .body(result);
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    })
            );
    }

    /**
     * {@code PUT  /telefonos/:id} : Updates an existing telefono.
     *
     * @param id the id of the telefono to save.
     * @param telefono the telefono to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telefono,
     * or with status {@code 400 (Bad Request)} if the telefono is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telefono couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/telefonos/{id}")
    public Mono<ResponseEntity<Telefono>> updateTelefono(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Telefono telefono
    ) throws URISyntaxException {
        log.debug("REST request to update Telefono : {}, {}", id, telefono);
        if (telefono.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telefono.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telefonoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return telefonoRepository
                    .save(telefono)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /telefonos/:id} : Partial updates given fields of an existing telefono, field will ignore if it is null
     *
     * @param id the id of the telefono to save.
     * @param telefono the telefono to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telefono,
     * or with status {@code 400 (Bad Request)} if the telefono is not valid,
     * or with status {@code 404 (Not Found)} if the telefono is not found,
     * or with status {@code 500 (Internal Server Error)} if the telefono couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/telefonos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Telefono>> partialUpdateTelefono(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Telefono telefono
    ) throws URISyntaxException {
        log.debug("REST request to partial update Telefono partially : {}, {}", id, telefono);
        if (telefono.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telefono.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return telefonoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Telefono> result = telefonoRepository
                    .findById(telefono.getId())
                    .map(existingTelefono -> {
                        if (telefono.getCodigoArea() != null) {
                            existingTelefono.setCodigoArea(telefono.getCodigoArea());
                        }
                        if (telefono.getNroTelefono() != null) {
                            existingTelefono.setNroTelefono(telefono.getNroTelefono());
                        }
                        if (telefono.getTipo() != null) {
                            existingTelefono.setTipo(telefono.getTipo());
                        }

                        return existingTelefono;
                    })
                    .flatMap(telefonoRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /telefonos} : get all the telefonos filtering by nombre and apellido.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of telefonos in body.
     */
    @GetMapping("/telefonos")
    public Mono<List<Telefono>> getAllTelefonos(@RequestParam(required = false) String nombre, @RequestParam(required = false) String apellido) {
        log.debug("REST request to get all Telefonos filtering by {}, {}", nombre, apellido);
        if (nombre == null || apellido == null) {
            log.debug("REST request to get all Telefonos");
            return telefonoService.getTelefonos().collectList();
        }
        log.debug("REST request to get a telefono from {}, {}", nombre, apellido);
        String nombreRegex = ".*" + nombre + ".*";
        String apellidoRegex = ".*" + apellido + ".*";
        return telefonoService.getTelefonos(nombreRegex, apellidoRegex).collectList();
    }

    /**
     * {@code GET  /telefonos} : get all the telefonos as a stream.
     * @return the {@link Flux} of telefonos.
     */
    @GetMapping(value = "/telefonos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Telefono> getAllTelefonosAsStream() {
        log.debug("REST request to get all Telefonos as a stream");
        return telefonoRepository.findAll();
    }

    /**
     * {@code GET  /telefonos/:id} : get the "id" telefono.
     *
     * @param id the id of the telefono to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telefono, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/telefonos/{id}")
    public Mono<ResponseEntity<Telefono>> getTelefono(@PathVariable String id) {
        log.debug("REST request to get Telefono : {}", id);
        Mono<Telefono> telefono = telefonoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(telefono);
    }

    /**
     * {@code DELETE  /telefonos/:id} : delete the "id" telefono.
     *
     * @param id the id of the telefono to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/telefonos/{id}")
    public Mono<ResponseEntity<Void>> deleteTelefono(@PathVariable String id) {
        log.debug("REST request to delete Telefono : {}", id);
        return telefonoRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
                )
            );
    }
}
