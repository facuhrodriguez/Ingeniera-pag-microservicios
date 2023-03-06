package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGastoTotalConIvaDTO implements Serializable {
    private final List<GastoTotalConIvaDTO> clientes = new ArrayList<>();

    public List<GastoTotalConIvaDTO> getClientes() {
        return clientes;
    }
}
