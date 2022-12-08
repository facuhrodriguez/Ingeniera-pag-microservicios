package com.ingenieria.producto.service.dto.getprecio;

import java.io.Serializable;
import java.util.List;

public class ProductoListDTO implements Serializable {

    private List<Long> productoList;

    public List<Long> getProductoList() {
        return productoList;
    }

    public void setProductoList(List<Long> productoList) {
        this.productoList = productoList;
    }
}
