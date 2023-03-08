package com.ingenieria.cliente.service.dto.getcantfacturas;

import java.io.Serializable;

public class ClienteCantFacturasDTO implements Serializable {

    private String clientId;
    private Long cantFacturas;

    public ClienteCantFacturasDTO(String clientId, Long cantFacturas) {
        this.clientId = clientId;
        this.cantFacturas = cantFacturas;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getCantFacturas() {
        return cantFacturas;
    }

    public void setCantFacturas(Long cantFacturas) {
        this.cantFacturas = cantFacturas;
    }

}
