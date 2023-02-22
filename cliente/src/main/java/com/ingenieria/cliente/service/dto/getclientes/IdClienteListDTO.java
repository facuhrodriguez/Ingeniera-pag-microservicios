package com.ingenieria.cliente.service.dto.getclientes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IdClienteListDTO implements Serializable {
    private final List<String> idCliente = new ArrayList<>();

    public List<String> getIdCliente() {
        return idCliente;
    }

}

