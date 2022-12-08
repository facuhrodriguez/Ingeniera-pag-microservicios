package com.ingenieria.producto.service.dto.getprecio;

import java.io.Serializable;
import java.util.Objects;

public class ProductoPrecioDTO implements Serializable {

    private Long id;
    private Float precio;

    public ProductoPrecioDTO(Long id, Float precio) {
        this.id = id;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoPrecioDTO that = (ProductoPrecioDTO) o;
        return id.equals(that.id) && precio.equals(that.precio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, precio);
    }
}
