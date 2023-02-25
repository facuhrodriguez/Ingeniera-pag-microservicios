package com.ingenieria.factura.service.dto.getprecio;

import java.io.Serializable;
import java.util.List;

public class ProductoListDTO implements Serializable {

    private List<Long> productoList;

    public ProductoListDTO() {
    }

    public ProductoListDTO(List<Long> productoList) {
        this.productoList = productoList;
    }

    public List<Long> getProductoList() {
        return productoList;
    }

    public void setProductoList(List<Long> productoList) {
        this.productoList = productoList;
    }
}
