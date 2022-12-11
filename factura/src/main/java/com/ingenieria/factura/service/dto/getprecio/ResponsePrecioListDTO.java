package com.ingenieria.factura.service.dto.getprecio;

import java.io.Serializable;
import java.util.HashMap;

public class ResponsePrecioListDTO implements Serializable {
    private HashMap<Long, Float> precios;

    public ResponsePrecioListDTO(){}

    public ResponsePrecioListDTO(HashMap<Long, Float> precios) {
        this.precios = precios;
    }

    public HashMap<Long, Float> getPrecios() {
        return precios;
    }

    public void setPrecios(HashMap<Long, Float> precios) {
        this.precios = precios;
    }

}
