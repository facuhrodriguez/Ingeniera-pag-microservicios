package com.ingenieria.factura.service;

import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.FacturaRepository;
import com.ingenieria.factura.service.dto.getfacturas.IdClienteListDTO;
import com.ingenieria.factura.service.dto.getprecio.ProductoListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

@Service
@Transactional
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(FacturaService.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtBase64;

    public FacturaService(FacturaRepository facturaRepository, DiscoveryClient discoveryClient, WebClient webClient) {
        this.facturaRepository = facturaRepository;
        this.discoveryClient = discoveryClient;
        this.webClient = webClient;
    }

    public Flux<Factura> findAllFacturas(String nombre, String apellido) {
        log.info("Factura service: finding all using strings {}, {}", nombre, apellido);

        var msClienteInstance = discoveryClient.getInstances("cliente").get(0);

        var uri = URI.create(String.format("%s/api/clientes/ids?nombre=%s&apellido=%s", msClienteInstance.getUri(), nombre, apellido));

        return webClient.get()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", jwtBase64))
            .retrieve()
            .bodyToMono(IdClienteListDTO.class)
            .map((idClienteListDTO) -> {
                List<String> ids = idClienteListDTO.getIdCliente();
                log.debug("Factura service: id_clients obtenidos {}", ids);
                return ids;
            })
            .map(ids ->
                Flux.fromStream(ids.stream())
                    .concatMap(facturaRepository::findAllByIdCliente))
            .flatMapMany(facturaFlux -> facturaFlux);

    }

    public Flux<Factura> findAllFacturas(String marca) {
        log.info("Factura service: finding all using string {}", marca);

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(String.format("%s/api/productos/ids?marca=%s", msProductoInstance.getUri(), marca));

        return webClient.get()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", jwtBase64))
            .retrieve()
            .bodyToMono(ProductoListDTO.class)
            .map(productoListDTO ->
            {
                log.debug("All products obtained: {}", productoListDTO.getProductoList());
                return Flux.fromStream(productoListDTO.getProductoList().stream())
                    .concatMap(facturaRepository::getAllFacturas);
            })
            .flatMapMany(facturaFlux -> facturaFlux)
            .distinct()
            .doOnComplete(() -> log.debug("All Facturas were obtained"));

    }

}
