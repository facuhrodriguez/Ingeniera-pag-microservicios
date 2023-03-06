package com.ingenieria.cliente.web.rest;

import com.ingenieria.cliente.domain.Cliente;
import com.ingenieria.cliente.domain.GastoTotalIva;
import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.repository.GastoConIvaRepository;
import com.ingenieria.cliente.service.ClienteService;
import com.ingenieria.cliente.service.dto.getclientes.IdClienteListDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.GastoTotalConIvaDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.ListNombreApellidoGastoTotalConIvaDTO;
import com.ingenieria.cliente.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing {@link com.ingenieria.cliente.domain.Cliente}.
 */
@RestController
@RequestMapping("/api")
public class ClienteResource {

    private final Logger log = LoggerFactory.getLogger(ClienteResource.class);

    private static final String ENTITY_NAME = "clienteCliente";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final GastoConIvaRepository gastoConIvaRepository;

    public ClienteResource(ClienteService clienteService, ClienteRepository clienteRepository, GastoConIvaRepository gastoConIvaRepository) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.gastoConIvaRepository = gastoConIvaRepository;
    }

    /**
     * {@code POST  /clientes} : Create a new cliente.
     *
     * @param cliente the cliente to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cliente, or with status {@code 400 (Bad Request)} if the cliente has already an ID.
     */
    @PostMapping("/clientes")
    public Mono<ResponseEntity<Cliente>> createCliente(@RequestBody Cliente cliente) {
        log.debug("REST request to save Cliente : {}", cliente);
        if (cliente.getId() != null) {
            throw new BadRequestAlertException("A new cliente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return clienteRepository
            .save(cliente)
            .flatMap(cliente1 -> gastoConIvaRepository.save(new GastoTotalIva().cliente(cliente1).gastoTotalIva(0.))
                .then(Mono.just(cliente1)))
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/clientes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /clientes/:id} : Updates an existing cliente.
     *
     * @param id the id of the cliente to save.
     * @param cliente the cliente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cliente,
     * or with status {@code 400 (Bad Request)} if the cliente is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cliente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clientes/{id}")
    public Mono<ResponseEntity<Cliente>> updateCliente(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Cliente cliente
    ) throws URISyntaxException {
        log.debug("REST request to update Cliente : {}, {}", id, cliente);
        if (cliente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cliente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clienteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return clienteRepository
                    .save(cliente)
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
     * {@code PATCH  /clientes/:id} : Partial updates given fields of an existing cliente, field will ignore if it is null
     *
     * @param id the id of the cliente to save.
     * @param cliente the cliente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cliente,
     * or with status {@code 400 (Bad Request)} if the cliente is not valid,
     * or with status {@code 404 (Not Found)} if the cliente is not found,
     * or with status {@code 500 (Internal Server Error)} if the cliente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clientes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Cliente>> partialUpdateCliente(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Cliente cliente
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cliente partially : {}, {}", id, cliente);
        if (cliente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cliente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clienteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Cliente> result = clienteRepository
                    .findById(cliente.getId())
                    .map(existingCliente -> {
                        if (cliente.getNombre() != null) {
                            existingCliente.setNombre(cliente.getNombre());
                        }
                        if (cliente.getApellido() != null) {
                            existingCliente.setApellido(cliente.getApellido());
                        }
                        if (cliente.getDireccion() != null) {
                            existingCliente.setDireccion(cliente.getDireccion());
                        }
                        if (cliente.getActivo() != null) {
                            existingCliente.setActivo(cliente.getActivo());
                        }

                        return existingCliente;
                    })
                    .flatMap(clienteRepository::save);

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
     * {@code GET  /clientes} : get all the clientes with their telefono.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientes in body.
     */
    @GetMapping("/clientes")
    public Mono<List<Cliente>> getAllClientes() {
        log.debug("REST request to get all Clientes");
        return clienteService.getAll().collectList();
    }

    /**
     * {@code GET  /clientes/ids?nombre&apellido} : get a client filtering by nombre & apellido.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientes in body.
     */
    @GetMapping("/clientes/ids")
    public Mono<IdClienteListDTO> getAllIdClientes(@RequestParam(required = false) String nombre, @RequestParam(required = false) String apellido) {
        IdClienteListDTO idList = new IdClienteListDTO();
        if (nombre == null || apellido == null) {
            log.debug("REST request to get all Clientes");
            return clienteRepository.findAll()
                .doOnNext(cliente -> idList.getIdCliente().add(cliente.getId()))
                .then(Mono.just(idList));
        }
        log.debug("REST request to get a client like {}, {}", nombre, apellido);
        String nombreRegex = ".*" + nombre + ".*";
        String apellidoRegex = ".*" + apellido + ".*";
        return clienteRepository.findByNombreRegexAndApellidoRegex(nombreRegex, apellidoRegex)
            .doOnNext(cliente -> idList.getIdCliente().add(cliente.getId()))
            .then(Mono.just(idList));
    }

    /**
     * {@code GET  /clientes/:id} : get the "id" Cliente with their Telefono.
     *
     * @param id the id of the cliente to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cliente, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clientes/{id}")
    public Mono<ResponseEntity<Cliente>> getCliente(@PathVariable String id) {
        log.debug("REST request to get Cliente : {}", id);
        Mono<Cliente> cliente = clienteService.getClientWithTelefono(id);
        return ResponseUtil.wrapOrNotFound(cliente);
    }

    /**
     * {@code DELETE  /clientes/:id} : delete the "id" cliente.
     *
     * @param id the id of the cliente to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clientes/{id}")
    public Mono<ResponseEntity<Void>> deleteCliente(@PathVariable String id) {
        log.debug("REST request to delete Cliente : {}", id);
        return clienteRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
                )
            );
    }

    /**
     * {@code GET  /clientes/con-gasto-total-iva} : obtain the total expense with VAT of all the invoices for each client.
     *
     * @return the {@link List} with status {@code 200 (OK)} and with body
     * the clientes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clientes/con-gasto-total-iva")
    public Mono<ListNombreApellidoGastoTotalConIvaDTO> getClientesAndGastoTotalConIva() {
        log.debug("REST request to get Cliente with Gasto Total with IVA");
        ListNombreApellidoGastoTotalConIvaDTO r = new ListNombreApellidoGastoTotalConIvaDTO();
        return clienteService.getClientesGastoTotalConIvaService()
            .collectList()
            .doOnSuccess(r::setClientes)
            .then(Mono.just(r));
    }

    /**
     * {@code PUT  /clientes/con-gasto-total-iva} : update gasto total iva.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the new
     * sum of gasto total iva client in body.
     */
    @PutMapping("/clientes/con-gasto-total-iva")
    public Mono<GastoTotalConIvaDTO> getClientesAndGastoTotalConIva(@RequestBody GastoTotalConIvaDTO gastoTotalConIvaDTO) {
        log.debug("REST request to inform a new expense from client id {}", gastoTotalConIvaDTO.getClienteId());
        return clienteService.sumGastoTotalIva(gastoTotalConIvaDTO);
    }

    /**
     * {@code GET  /clientes/clientes-sin-facturas} : get all the clients without facturas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of clients in body.
     */
    @GetMapping("/clientes/sin-facturas")
    public Flux<Cliente> getClientsWithoutFacturas() {
        log.debug("REST request to get Cliente without Facturas");
        return clienteService.getClientsWithoutFacturas();
    }

}
