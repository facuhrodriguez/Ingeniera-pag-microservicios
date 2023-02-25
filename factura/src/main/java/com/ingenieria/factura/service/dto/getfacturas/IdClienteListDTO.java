package com.ingenieria.factura.service.dto.getfacturas;

import java.io.Serializable;
import java.util.List;

public class IdClienteListDTO implements Serializable {
    private List<String> idCliente;

    public List<String> getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(List<String> idCliente) {
        this.idCliente = idCliente;
    }
}
