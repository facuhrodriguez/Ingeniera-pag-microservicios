package com.ingenieria.producto.service.dto.getprecio;

import java.io.Serializable;
import java.util.List;

public class ResponsePrecioListDTO implements Serializable {
    private List<ProductoPrecioDTO> precios;

    public ResponsePrecioListDTO(List<ProductoPrecioDTO> precios) {
        this.precios = precios;
    }

    public List<ProductoPrecioDTO> getPrecios() {
        return precios;
    }

    public void setPrecios(List<ProductoPrecioDTO> precios) {
        this.precios = precios;
    }

}
