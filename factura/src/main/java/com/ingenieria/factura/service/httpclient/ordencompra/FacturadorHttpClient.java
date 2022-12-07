package com.ingenieria.factura.service.httpclient.ordencompra;

import com.ingenieria.factura.service.FacturadorService;
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

    public long checkAllStock(OrdenCompraDTO ordenCompraDTO) throws WebClientException {
        log.debug("FacturadorHttpClient: solicitando chequear el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/check-all-stock");

        JSONObject jsonResult = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(ordenCompraDTO.toString())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block(); // wait for the async request

        assert jsonResult != null;
        // json = { "idSolicitud": 123124 }
        long idSolicitud = new JSONObject(jsonResult).getLong("idSolicitud");

        log.debug("FacturadorHttpClient: stock checkeado! idSolicitud={}", idSolicitud);

        return idSolicitud;
    }

    public Mono<String> decreaseAllStock(long idSolicitud) throws WebClientException {
        log.debug("FacturadorHttpClient: enviando solicitud para decrementar el stock...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/decrease-all-stock?idSolicitud=" + idSolicitud);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    public JSONObject getPrices(String listaProductos) {
        log.debug("FacturadorHttpClient: enviando solicitud para obtener listado de precios...");

        var msProductoInstance = discoveryClient.getInstances("producto").get(0);

        var uri = URI.create(msProductoInstance.getUri() + "/productos/get-prices");


        JSONObject jsonResult = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .bodyValue(listaProductos)
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        assert jsonResult != null;
        // jsonResult = { "productid1": 10.0,
        //                "productid2": 20.0
        //              }

        log.debug("FacturadorHttpClient: listado de precios obtenido!");

        return jsonResult;
    }

}
