package com.ingenieria.factura.service.httpclient.ordencompra;

import com.ingenieria.factura.service.FacturadorService;
import com.ingenieria.factura.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.factura.service.dto.getprecio.ResponsePrecioListDTO;
import com.ingenieria.factura.service.dto.ordencompra.IdSolicitudDTO;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.net.URI;


@Component
public class FacturadorHttpClient {

    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(FacturadorService.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtBase64;

    public FacturadorHttpClient(DiscoveryClient discoveryClient, WebClient webClient) {
        this.discoveryClient = discoveryClient;
        this.webClient = webClient;
    }

    public Mono<Long> checkAllStock(OrdenCompraDTO ordenCompraDTO) throws WebClientException {
        log.debug("FacturadorHttpClient: solicitando chequear el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/api/productos/check-all-stock");

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", jwtBase64))
                .bodyValue(ordenCompraDTO)
                .retrieve()
                .bodyToMono(IdSolicitudDTO.class)
                .flatMap((idSolicitudDTO) -> {
                    // { "idSolicitud": 123124 }
                    Long idSolicitud = idSolicitudDTO.getIdSolicitud();
                    log.debug("FacturadorHttpClient: stock checkeado! idSolicitud={}", idSolicitud);
                    return Mono.just(idSolicitud);
                });

    }

    public Mono<Void> decrementarStock(Long idSolicitud) throws WebClientException {
        log.debug("FacturadorHttpClient: enviando solicitud para decrementar el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(String.format("%s/api/productos/decrementar-stock?idSolicitud=%d", msProductoInstance.getUri(), idSolicitud));

        return webClient.get()
            .uri(uri)
            .header("Authorization", String.format("Bearer %s", jwtBase64))
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess((mono) -> log.debug("Facturador service: stock decrementado! id={}", idSolicitud))
            .doOnError(e -> log.debug("ERROR GET: ", e));
    }

    public Mono<ResponsePrecioListDTO> getPrices(ProductoListDTO productos) throws WebClientException {
        log.debug("FacturadorHttpClient: enviando solicitud para obtener listado de precios...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/api/productos/get-precios");

        // json= {
        // 		   precios: HashMap {"id": "productid1", precio: 10.0} , {"id": "productid2", precio: 20.0}
        //       }

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", jwtBase64))
                .bodyValue(productos)
                .retrieve()
                .bodyToMono(ResponsePrecioListDTO.class)
                .doOnSuccess((r) -> log.debug("FacturadorHttpClient: listado de precios obtenido!"));
    }

}
