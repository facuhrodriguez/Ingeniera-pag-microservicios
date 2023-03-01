package com.ingenieria.factura.web.rest;

import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.FacturaRepository;
import com.ingenieria.factura.service.FacturaService;
import com.ingenieria.factura.service.FacturadorService;
import com.ingenieria.factura.service.dto.getfacturas.IdClienteListDTO;
import com.ingenieria.factura.service.dto.getgastototalconiva.GastoTotalConIvaDTO;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.factura.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * REST controller for managing {@link com.ingenieria.factura.domain.Factura}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FacturaResource {

    private final Logger log = LoggerFactory.getLogger(FacturaResource.class);

    private static final String ENTITY_NAME = "facturaFactura";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacturaRepository facturaRepository;
    private final FacturadorService facturadorService;
    private final FacturaService facturaService;

    public FacturaResource(FacturaRepository facturaRepository, FacturadorService facturadorService, FacturaService facturaService) {
        this.facturaRepository = facturaRepository;
        this.facturadorService = facturadorService;
        this.facturaService = facturaService;
    }

    /**
     * {@code POST  /facturas} : Create a new factura.
     *
     * @param factura the factura to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new factura, or with status {@code 400 (Bad Request)} if the factura has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/facturas")
    public Mono<ResponseEntity<Factura>> createFactura(@RequestBody Factura factura) throws URISyntaxException {
        log.debug("REST request to save Factura : {}", factura);
        if (factura.getId() != null) {
            throw new BadRequestAlertException("A new factura cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return facturaRepository
            .save(factura)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/facturas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /facturas/:id} : Updates an existing factura.
     *
     * @param id the id of the factura to save.
     * @param factura the factura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factura,
     * or with status {@code 400 (Bad Request)} if the factura is not valid,
     * or with status {@code 500 (Internal Server Error)} if the factura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/facturas/{id}")
    public Mono<ResponseEntity<Factura>> updateFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Factura factura
    ) throws URISyntaxException {
        log.debug("REST request to update Factura : {}, {}", id, factura);
        if (factura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return facturaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return facturaRepository
                    .save(factura)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /facturas/:id} : Partial updates given fields of an existing factura, field will ignore if it is null
     *
     * @param id the id of the factura to save.
     * @param factura the factura to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factura,
     * or with status {@code 400 (Bad Request)} if the factura is not valid,
     * or with status {@code 404 (Not Found)} if the factura is not found,
     * or with status {@code 500 (Internal Server Error)} if the factura couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/facturas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Factura>> partialUpdateFactura(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Factura factura
    ) throws URISyntaxException {
        log.debug("REST request to partial update Factura partially : {}, {}", id, factura);
        if (factura.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factura.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return facturaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Factura> result = facturaRepository
                    .findById(factura.getId())
                    .map(existingFactura -> {
                        if (factura.getFecha() != null) {
                            existingFactura.setFecha(factura.getFecha());
                        }
                        if (factura.getTotalSinIva() != null) {
                            existingFactura.setTotalSinIva(factura.getTotalSinIva());
                        }
                        if (factura.getIva() != null) {
                            existingFactura.setIva(factura.getIva());
                        }
                        if (factura.getTotalConIva() != null) {
                            existingFactura.setTotalConIva(factura.getTotalConIva());
                        }
                        if (factura.getIdCliente() != null) {
                            existingFactura.setIdCliente(factura.getIdCliente());
                        }

                        return existingFactura;
                    })
                    .flatMap(facturaRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /facturas?nombre&apellido} : get all the facturas filtering by an optional name and lastname params.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of facturas in body.
     */
    @GetMapping("/facturas")
    public Flux<Factura> getAllFacturas(@RequestParam(required = false) String nombre,
                                        @RequestParam(required = false) String apellido,
                                        @RequestParam(required = false) String marca) {
        if ((nombre == null || apellido == null) && marca == null) {
            log.debug("REST request to get all Facturas");
            return facturaRepository.findAll();
        }
        if (marca != null) {
            log.debug("REST request to get all Facturas with marca like {}", marca);
            return facturaService.findAllFacturas(marca);
        }
        log.debug("REST request to get all Facturas with client owner like {}, {}", nombre, apellido);
        return facturaService.findAllFacturas(nombre, apellido);
    }

    /**
     * {@code GET  /facturas/:id} : get the "id" factura.
     *
     * @param id the id of the factura to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the factura, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/facturas/{id}")
    public Mono<ResponseEntity<Factura>> getFactura(@PathVariable Long id) {
        log.debug("REST request to get Factura : {}", id);
        Mono<Factura> factura = facturaService.findFacturaWithDetalles(id);
        return ResponseUtil.wrapOrNotFound(factura);
    }

    /**
     * {@code DELETE  /facturas/:id} : delete the "id" factura.
     *
     * @param id the id of the factura to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/facturas/{id}")
    public Mono<ResponseEntity<Void>> deleteFactura(@PathVariable Long id) {
        log.debug("REST request to delete Factura : {}", id);
        return facturaRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code POST  /facturas/facturar} : Facturar un cliente.
     *
     * @param idCliente the client to facturate.
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body
     * the new factura, or with status {@code 400 (Bad Request)} if the
     * cliente has already an ID.
     */
    @PostMapping("/facturas/facturar")
    public Mono<ResponseEntity<Factura>> facturarCompra(@RequestParam String idCliente, @RequestBody OrdenCompraDTO ordenCompraDTO)
        throws BadRequestAlertException {
        log.info("REST client request to facturar productos : {}", idCliente);

        // llamar al servicio encargado de facturar
        // enviar respuesta con la factura creada

        return facturadorService.run(idCliente, ordenCompraDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/facturas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code GET  /facturas/con-gasto-total-iva/:id} : get the "id" client total expenses with VAI.
     *
     * @param id the id of the factura to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the factura, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/facturas/con-gasto-total-iva/{id}")
    public Mono<GastoTotalConIvaDTO> getFactura(@PathVariable String id) {
        log.info("REST request to get the id client total expenses with VAI: {}", id);
        return this.facturaRepository.getGastoTotalConIva(id)
            .map(GastoTotalConIvaDTO::new);
    }

    /**
     * {@code GET  /facturas/clients} : get all the clientes "id" with at least one factura.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of clientes in body.
     */
    @GetMapping("/facturas/clients")
    public Mono<IdClienteListDTO> getClientesWithFacturas() {
        IdClienteListDTO r = new IdClienteListDTO();
        return facturaRepository.getAllIdClients()
            .collectList()
            .doOnNext(r::setIdCliente)
            .then(Mono.just(r));
    }

}
