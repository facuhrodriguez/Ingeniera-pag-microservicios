package com.ingenieria.producto.service;

import com.ingenieria.producto.repository.ProductoRepository;
import com.ingenieria.producto.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.producto.service.dto.getprecio.ProductoPrecioDTO;
import com.ingenieria.producto.service.dto.ordencompra.OrdenCompraDTO;
import com.ingenieria.producto.service.errors.ProductoNoRegistradoException;
import com.ingenieria.producto.service.errors.StockInsuficienteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final HashMap<Long, OrdenCompraDTO> ordenesDeCompraTemp = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(ProductoService.class);

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Mono<Long> checkAllStock(OrdenCompraDTO ordenCompraDTO) {
        log.debug("Producto service: por chequear productos {}", ordenCompraDTO);
        // generar un número aleatorio
        long idSolicitud = (long) (Math.random() * 1000000);

        // recorrer colección y verificar el stock disponible
        return Flux.fromIterable(ordenCompraDTO.getProductoCantidadList())
                .flatMap((productoCantidadDTO) -> {
                    long codigoProducto = productoCantidadDTO.getId();

                    // chequear que el producto existe y esté en stock
                    return productoRepository
                            .findById(codigoProducto)
                            .switchIfEmpty(Mono.error(new ProductoNoRegistradoException(codigoProducto)))
                            .filter((productoRegistrado) -> !(productoRegistrado.getStock() < productoCantidadDTO.getCantidad()))
                            .switchIfEmpty(Mono.error(new StockInsuficienteException(codigoProducto)));
                })
                .doOnComplete(() -> {

                    // almacenar temporalmente la orden de compra para evitar volver a recibirla en otra solicitud.
                    ordenesDeCompraTemp.put(idSolicitud, ordenCompraDTO);

                    log.debug("Producto service: productos chequeados, idSolicitud={}", idSolicitud);

                })
                .then(Mono.just(idSolicitud));

    }

    public Mono<Void> decrementarStock(long idSolicitud) {
        log.debug("Producto service: por decrementar productos, idSolicitud={}", idSolicitud);

        // obtener la orden de compra almacenada previamente
        OrdenCompraDTO ordenCompra = ordenesDeCompraTemp.get(idSolicitud);

        // recorrer de forma asíncrona los productos y decrementar stock de forma reactiva
        return Flux.fromIterable(ordenCompra.getProductoCantidadList())
                .flatMap((productoCantidadDTO) -> {
                    long codigoProducto = productoCantidadDTO.getId();

                    return productoRepository
                            .findById(codigoProducto)
                            .doOnSuccess((productoRegistrado) -> {
                                // decrementar stock
                                Integer nuevoStock = productoRegistrado.getStock() - productoCantidadDTO.getCantidad();
                                productoRegistrado.setStock(nuevoStock);
                            }) // guardar cambios en persistencia
                            .doOnSuccess((productoRepository::save));
                })
                .doOnComplete(() -> {
                    ordenesDeCompraTemp.remove(idSolicitud);
                    log.debug("Producto service: decremento finalizado idSolicitud={}", idSolicitud);
                })
                .then(Mono.empty());

    }

    public Mono<HashMap<Long, Float>> getPrecios(ProductoListDTO productos) {
        log.debug("Producto service: por obtener listado de precios");

        return Flux.fromIterable(productos.getProductoList())
                .flatMap((codigoProducto) -> productoRepository
                        .findById(codigoProducto)
                        .switchIfEmpty(Mono.error(new ProductoNoRegistradoException(codigoProducto)))
                        .map(productoEntity ->
                                new ProductoPrecioDTO(codigoProducto, productoEntity.getPrecio()))
                )
                .doOnComplete(() ->
                        log.debug("Producto service: retornando listado de precios"))
                .collect(Collectors.toMap(ProductoPrecioDTO::getId, ProductoPrecioDTO::getPrecio, Float::sum, HashMap::new));

    }

}
