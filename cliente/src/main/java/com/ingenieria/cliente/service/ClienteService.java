package com.ingenieria.cliente.service;

import com.ingenieria.cliente.domain.Cliente;
import com.ingenieria.cliente.domain.Telefono;
import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.repository.TelefonoRepository;
import com.ingenieria.cliente.service.dto.getclientes.IdClienteListDTO;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TelefonoRepository telefonoRepository;
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtBase64;

    public ClienteService(ClienteRepository clienteRepository, TelefonoRepository telefonoRepository, DiscoveryClient discoveryClient, WebClient webClient) {
        this.clienteRepository = clienteRepository;
        this.telefonoRepository = telefonoRepository;
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

    public Flux<Cliente> getAll() {
        return clienteRepository.findAll()
            .flatMap(cliente -> getClientWithTelefono(cliente.getId()))
            .collectList()
            .flatMapMany(Flux::fromIterable);
    }

    public Mono<Cliente> getClientWithTelefono(String id) {
        return clienteRepository.findById(id)
            .flatMap(cliente -> {
                Mono<Telefono> telefonoMono = telefonoRepository.findByCliente_Id(cliente.getId())
                    .singleOrEmpty();
                return telefonoMono.map(cliente::telefono)
                    .defaultIfEmpty(cliente);
            });
    }

    public Flux<Cliente> getClientsWithoutFacturas() {
        log.info("Cliente service: searching all clients along without facturas registered");

        var msFacturaInstance = discoveryClient.getInstances("factura").get(0);

        var uri = String.format("%s/api/facturas/clients", msFacturaInstance.getUri());

        return webClient.get()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", jwtBase64))
            .retrieve()
            .bodyToMono(IdClienteListDTO.class)
            .map(idClienteListDTO -> clienteRepository.findAll()
                .filter(cliente -> !idClienteListDTO.getIdCliente()
                    .contains(cliente.getId())
                )
            ).flatMapMany(clienteFlux -> clienteFlux);
    }

}
