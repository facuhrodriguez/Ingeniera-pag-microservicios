package com.ingenieria.factura.service.dto.getgastototalconiva;

import java.io.Serializable;

public class GastoTotalConIvaDTO implements Serializable {

    private final Double gastoTotal;

    public GastoTotalConIvaDTO(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }

}
