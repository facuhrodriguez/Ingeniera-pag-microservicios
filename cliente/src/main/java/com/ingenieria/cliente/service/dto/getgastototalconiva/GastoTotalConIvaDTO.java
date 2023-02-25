package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;

public class GastoTotalConIvaDTO implements Serializable {

    private Double gastoTotal;

    public GastoTotalConIvaDTO() {
    }

    public GastoTotalConIvaDTO(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }

    public Double getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }
}
