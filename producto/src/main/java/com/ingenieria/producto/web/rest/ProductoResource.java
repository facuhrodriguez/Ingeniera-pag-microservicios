package com.ingenieria.producto.web.rest;

import com.ingenieria.producto.domain.Producto;
import com.ingenieria.producto.repository.ProductoRepository;
import com.ingenieria.producto.service.ProductoService;
import com.ingenieria.producto.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.producto.service.dto.getprecio.ResponsePrecioListDTO;
import com.ingenieria.producto.service.dto.ordencompra.IdSolicitudDTO;
import com.ingenieria.producto.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.producto.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ingenieria.producto.domain.Producto}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductoResource {

    private final Logger log = LoggerFactory.getLogger(ProductoResource.class);

    private static final String ENTITY_NAME = "productoProducto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public ProductoResource(ProductoRepository productoRepository, ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    /**
     * {@code POST  /productos} : Create a new producto.
     *
     * @param producto the producto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new producto, or with status {@code 400 (Bad Request)} if the producto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/productos")
    public Mono<ResponseEntity<Producto>> createProducto(@RequestBody Producto producto) throws URISyntaxException {
        log.debug("REST request to save Producto : {}", producto);
        if (producto.getId() != null) {
            throw new BadRequestAlertException("A new producto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productoRepository
            .save(producto)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/productos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /productos/:id} : Updates an existing producto.
     *
     * @param id the id of the producto to save.
     * @param producto the producto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated producto,
     * or with status {@code 400 (Bad Request)} if the producto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the producto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/productos/{id}")
    public Mono<ResponseEntity<Producto>> updateProducto(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Producto producto
    ) throws URISyntaxException {
        log.debug("REST request to update Producto : {}, {}", id, producto);
        if (producto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, producto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productoRepository
                    .save(producto)
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
     * {@code PATCH  /productos/:id} : Partial updates given fields of an existing producto, field will ignore if it is null
     *
     * @param id the id of the producto to save.
     * @param producto the producto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated producto,
     * or with status {@code 400 (Bad Request)} if the producto is not valid,
     * or with status {@code 404 (Not Found)} if the producto is not found,
     * or with status {@code 500 (Internal Server Error)} if the producto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/productos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Producto>> partialUpdateProducto(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Producto producto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Producto partially : {}, {}", id, producto);
        if (producto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, producto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Producto> result = productoRepository
                    .findById(producto.getId())
                    .map(existingProducto -> {
                        if (producto.getMarca() != null) {
                            existingProducto.setMarca(producto.getMarca());
                        }
                        if (producto.getNombre() != null) {
                            existingProducto.setNombre(producto.getNombre());
                        }
                        if (producto.getDescripcion() != null) {
                            existingProducto.setDescripcion(producto.getDescripcion());
                        }
                        if (producto.getPrecio() != null) {
                            existingProducto.setPrecio(producto.getPrecio());
                        }
                        if (producto.getStock() != null) {
                            existingProducto.setStock(producto.getStock());
                        }

                        return existingProducto;
                    })
                    .flatMap(productoRepository::save);

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
     * {@code GET  /productos} : get all the productos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productos in body.
     */
    @GetMapping("/productos")
    public Mono<List<Producto>> getAllProductos() {
        log.debug("REST request to get all Productos");
        return productoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /productos} : get all the productos as a stream.
     * @return the {@link Flux} of productos.
     */
    @GetMapping(value = "/productos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Producto> getAllProductosAsStream() {
        log.debug("REST request to get all Productos as a stream");
        return productoRepository.findAll();
    }

    /**
     * {@code GET  /productos/:id} : get the "id" producto.
     *
     * @param id the id of the producto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the producto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/productos/{id}")
    public Mono<ResponseEntity<Producto>> getProducto(@PathVariable Long id) {
        log.debug("REST request to get Producto : {}", id);
        Mono<Producto> producto = productoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(producto);
    }

    /**
     * {@code DELETE  /productos/:id} : delete the "id" producto.
     *
     * @param id the id of the producto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/productos/{id}")
    public Mono<ResponseEntity<Void>> deleteProducto(@PathVariable Long id) {
        log.debug("REST request to delete Producto : {}", id);
        return productoRepository
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
     * {@code POST  /productos/check-all-stock} : check all stock payload.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the id request.
     */
    @PostMapping("/productos/check-all-stock")
    public Mono<ResponseEntity<IdSolicitudDTO>> checkAllStock(@RequestBody OrdenCompraDTO ordenCompraDTO) {
        log.debug("REST request to check all stock: {}", ordenCompraDTO);
        return productoService
                .checkAllStock(ordenCompraDTO)
                .map((idSolicitud) -> ResponseEntity
                        .ok()
                        .body(new IdSolicitudDTO(idSolicitud)));
    }

    /**
     * {@code GET  /productos/decrementar-stock} : decrementar stock.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the id request.
     */
    @GetMapping("/productos/decrementar-stock?idSolicitud")
    public Mono<ResponseEntity<Void>> decrementarStock(@RequestParam long idSolicitud) {
        log.debug("REST request to decrementar stock with id: {}", idSolicitud);
        return productoService
                .decrementarStock(idSolicitud)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * {@code POST  /productos/get-prices} : obtener listado de precios de productos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the id request.
     */
    @PostMapping("/productos/get-precios")
    public Mono<ResponseEntity<ResponsePrecioListDTO>> getPrecios(@RequestBody ProductoListDTO productoListDTO) {
        log.debug("REST request to get precios of: {}", productoListDTO);
        return productoService
            .getPrecios(productoListDTO)
            .map((precios) -> ResponseEntity
                .ok()
                .body(new ResponsePrecioListDTO(precios)));
    }

    /**
     * {@code GET  /productos/ids?marca} : get all id products filtering with marca param.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the id request.
     */
    @GetMapping("/productos/ids")
    public Mono<ProductoListDTO> getAllId(@RequestParam(required = false, defaultValue = "") String marca) {
        ProductoListDTO r = new ProductoListDTO();
        log.debug("REST request to get all product ids with marca: {}", marca);
        return productoRepository.getAllIdByMarca(marca)
            .map(id -> r.getProductoList().add(id))
            .then(Mono.just(r));
    }

}
