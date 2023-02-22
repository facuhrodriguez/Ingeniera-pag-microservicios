package com.ingenieria.cliente.service.getclientes;

import java.io.Serializable;
import java.util.List;

public class IdClienteListDTO implements Serializable {
    private List<Long> idCliente;

    public List<Long> getIdCliente() {
        return idCliente;
    }

}

