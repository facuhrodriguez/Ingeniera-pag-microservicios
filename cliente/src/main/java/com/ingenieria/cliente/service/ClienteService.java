package com.ingenieria.cliente.service;

import com.ingenieria.cliente.domain.Cliente;
import com.ingenieria.cliente.domain.GastoTotal;
import com.ingenieria.cliente.domain.Telefono;
import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.repository.GastoTotalRepository;
import com.ingenieria.cliente.repository.TelefonoRepository;
import com.ingenieria.cliente.service.dto.getcantfacturas.ClienteCantFacturasDTO;
import com.ingenieria.cliente.service.dto.getclientes.IdClienteListDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.GastoTotalConIvaDTO;
import com.ingenieria.cliente.service.dto.getgastototalconiva.NombreApellidoGastoTotalConIvaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final GastoTotalRepository gastoTotalRepository;

    private final ReactiveMongoTemplate mongoTemplate;
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtBase64;

    public ClienteService(ClienteRepository clienteRepository, TelefonoRepository telefonoRepository, GastoTotalRepository gastoTotalRepository, ReactiveMongoTemplate mongoTemplate, DiscoveryClient discoveryClient, WebClient webClient) {
        this.clienteRepository = clienteRepository;
        this.telefonoRepository = telefonoRepository;
        this.gastoTotalRepository = gastoTotalRepository;
        this.mongoTemplate = mongoTemplate;
        this.discoveryClient = discoveryClient;
        this.webClient = webClient;
    }

    public Flux<ClienteCantFacturasDTO> getClientsCantFacturas() {
        log.info("Cliente service: searching all customers along with their total facturas");
        return gastoTotalRepository.findAll()
            .flatMap((cliente_gastos -> Mono.just(new ClienteCantFacturasDTO(cliente_gastos.getCliente().getId(), cliente_gastos.getCantFacturas()))));
    }

    public Flux<NombreApellidoGastoTotalConIvaDTO> getClientesGastoTotalConIvaService() {
        log.info("Cliente service: searching all customers along with their expenses");
        return gastoTotalRepository.findAll()
            .flatMap((cliente_gastos -> Mono.just(new NombreApellidoGastoTotalConIvaDTO(cliente_gastos.getCliente().getNombre(), cliente_gastos.getCliente().getApellido(), cliente_gastos.getGastoTotalIva()))));
    }

    public Mono<GastoTotalConIvaDTO> sumGastoTotalIva(GastoTotalConIvaDTO gastoTotalConIvaDTO) {
        return gastoTotalRepository.findGastoTotalByCliente_Id(gastoTotalConIvaDTO.getClienteId())
            .flatMap(gasto -> {
                gasto.setGastoTotalIva(gasto.getGastoTotalIva() + gastoTotalConIvaDTO.getGastoTotal());
                gasto.setCantFacturas(gasto.getCantFacturas() + 1);
                return mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(gasto.getId())),
                        new Update()
                            .set("gasto_total_iva", gasto.getGastoTotalIva())
                            .set("cant_facturas", gasto.getCantFacturas()),
                        GastoTotal.class)
                    .thenReturn(gasto);
            })
            .flatMap(gastoTotal -> Mono.just(new GastoTotalConIvaDTO(gastoTotal.getCliente().getId(), gastoTotal.getGastoTotalIva())));
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
