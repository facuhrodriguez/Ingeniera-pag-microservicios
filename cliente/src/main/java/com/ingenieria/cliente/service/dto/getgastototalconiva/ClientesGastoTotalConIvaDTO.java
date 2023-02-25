package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientesGastoTotalConIvaDTO implements Serializable {

    private final List<ClienteGastoTotalConIvaDTO> clientes = new ArrayList<>();

    public List<ClienteGastoTotalConIvaDTO> getClientes() {
        return clientes;
    }

}
