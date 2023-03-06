package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;
import java.util.List;

public class ListNombreApellidoGastoTotalConIvaDTO implements Serializable {
    private List<NombreApellidoGastoTotalConIvaDTO> clientes;

    public ListNombreApellidoGastoTotalConIvaDTO() {
    }

    public List<NombreApellidoGastoTotalConIvaDTO> getClientes() {
        return clientes;
    }

    public void setClientes(List<NombreApellidoGastoTotalConIvaDTO> clientes) {
        this.clientes = clientes;
    }


}
