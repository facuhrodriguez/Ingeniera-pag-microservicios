package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;

public class GastoTotalConIvaDTO implements Serializable {

    private String clienteId;
    private Double gastoTotal;

    public GastoTotalConIvaDTO() {
    }

    public GastoTotalConIvaDTO(String clienteId, Double gastoTotal) {
        this.clienteId = clienteId;
        this.gastoTotal = gastoTotal;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public Double getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }
}
