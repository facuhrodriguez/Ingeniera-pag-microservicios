package com.ingenieria.cliente.service;

import com.ingenieria.cliente.domain.Cliente;
import com.ingenieria.cliente.domain.Telefono;
import com.ingenieria.cliente.repository.ClienteRepository;
import com.ingenieria.cliente.repository.TelefonoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional
public class TelefonoService {

    private final ClienteRepository clienteRepository;
    private final TelefonoRepository telefonoRepository;
    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    public TelefonoService(ClienteRepository clienteRepository, TelefonoRepository telefonoRepository) {
        this.clienteRepository = clienteRepository;
        this.telefonoRepository = telefonoRepository;
    }

    public Flux<Telefono> getTelefonos() {
        log.info("Cliente service: searching all telefonos");
        Flux<Cliente> clientes = clienteRepository.findAll();
        return mergeTelefonoAndClient(clientes);
    }

    public Flux<Telefono> getTelefonos(String nombreRegex, String apellidoRegex) {
        log.info("Cliente service: searching all telefonos with regex options");
        Flux<Cliente> clientes = clienteRepository.findByNombreRegexAndApellidoRegex(nombreRegex, apellidoRegex);
        return mergeTelefonoAndClient(clientes);
    }

    private Flux<Telefono> mergeTelefonoAndClient(Flux<Cliente> clientes) {
        Flux<Telefono> telefonos = clientes.flatMap(cliente ->
            telefonoRepository.findByCliente_Id(cliente.getId()));
        return Flux.zip(telefonos, clientes).map(tuple -> {
            Telefono telefono = tuple.getT1();
            Cliente cliente = tuple.getT2();
            telefono.setCliente(cliente);
            return telefono;
        });
    }
}
