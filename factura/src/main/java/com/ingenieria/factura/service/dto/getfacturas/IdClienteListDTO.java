package com.ingenieria.factura.service.dto.getfacturas;

import java.io.Serializable;
import java.util.List;

public class IdClienteListDTO implements Serializable {
    private List<Long> idCliente;

    public List<Long> getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(List<Long> idCliente) {
        this.idCliente = idCliente;
    }
}
