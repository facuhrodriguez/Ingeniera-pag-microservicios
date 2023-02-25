package com.ingenieria.cliente.service;

import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.service.dto.getgastototalconiva.ClienteGastoTotalConIvaDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.ClientesGastoTotalConIvaDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.GastoTotalConIvaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtBase64;

    public ClienteService(ClienteRepository clienteRepository, DiscoveryClient discoveryClient, WebClient webClient) {
        this.clienteRepository = clienteRepository;
        this.discoveryClient = discoveryClient;
        this.webClient = webClient;
    }

    public Mono<ClientesGastoTotalConIvaDTO> GetClientesGastoTotalConIvaService() {
        log.info("Cliente service: searching all customers along with their expenses");

        var msFacturaInstance = discoveryClient.getInstances("factura").get(0);

        var partialURI = String.format("%s/api/facturas/con-gasto-total-iva", msFacturaInstance.getUri());

        ClientesGastoTotalConIvaDTO r = new ClientesGastoTotalConIvaDTO();

        return clienteRepository.findAll()
            .flatMap((cliente) -> webClient.get()
                .uri(URI.create(String.format("%s/%s", partialURI, cliente.getId())))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", jwtBase64))
                .retrieve()
                .bodyToMono(GastoTotalConIvaDTO.class)
                .map(gastoTotalConIvaDTO ->
                {
                    log.debug("Client {} has ${} of expenses", cliente.getId(), gastoTotalConIvaDTO.getGastoTotal());
                    return r.getClientes().add(new ClienteGastoTotalConIvaDTO(cliente.getNombre(), cliente.getApellido(), gastoTotalConIvaDTO.getGastoTotal()));
                }))
            .doOnComplete(() -> log.debug("All Clients were obtained"))
            .then(Mono.just(r));

    }
}
