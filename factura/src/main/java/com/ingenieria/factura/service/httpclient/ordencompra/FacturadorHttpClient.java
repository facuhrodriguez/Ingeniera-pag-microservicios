package com.ingenieria.factura.service.httpclient.ordencompra;

import com.ingenieria.factura.service.FacturadorService;
import com.ingenieria.factura.service.dto.getprecio.ProductoListDTO;
import com.ingenieria.factura.service.dto.getprecio.ResponsePrecioListDTO;
import com.ingenieria.factura.service.dto.ordencompra.OrdenCompraDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public FacturadorHttpClient(DiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClient = webClientBuilder.build();
    }

    public Mono<Long> checkAllStock(OrdenCompraDTO ordenCompraDTO) throws WebClientException {
        log.debug("FacturadorHttpClient: solicitando chequear el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/check-all-stock");

        final long[] idSolicitud = {0L};

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(ordenCompraDTO.toString())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .doOnSuccess((jsonResult) -> {
                    // jsonResult = { "idSolicitud": 123124 }
                    idSolicitud[0] = (long) jsonResult.get("idSolicitud");
                    log.debug("FacturadorHttpClient: stock checkeado! idSolicitud={}", idSolicitud[0]);
                })
                .thenReturn(idSolicitud[0]);

    }

    public Mono<Void> decrementarStock(long idSolicitud) throws WebClientException {
        log.debug("FacturadorHttpClient: enviando solicitud para decrementar el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/decrementar-stock?idSolicitud=" + idSolicitud);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<ResponsePrecioListDTO> getPrices(ProductoListDTO productos) {
        log.debug("FacturadorHttpClient: enviando solicitud para obtener listado de precios...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/get-precios");

        // json= {
        // 		   precios: HashMap {"id": "productid1", precio: 10.0} , {"id": "productid2", precio: 20.0}
        //       }

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(productos)
                .retrieve()
                .bodyToMono(ResponsePrecioListDTO.class)
                .doOnSuccess((r) -> log.debug("FacturadorHttpClient: listado de precios obtenido!"));
    }

}
