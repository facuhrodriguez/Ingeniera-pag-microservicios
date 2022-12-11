package com.ingenieria.factura.service.dto.ordencompra;

import java.io.Serializable;

public class ProductoCantidadDTO implements Serializable {
    private Long id;
    private Integer cantidad;

    public ProductoCantidadDTO(Long id, Integer cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "{ id=" + id + ", cantidad=" + cantidad + " }";
    }

}
