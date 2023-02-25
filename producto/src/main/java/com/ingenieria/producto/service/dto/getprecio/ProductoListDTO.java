package com.ingenieria.producto.service.dto.getprecio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoListDTO implements Serializable {

    private List<Long> productoList = new ArrayList<>();

    public List<Long> getProductoList() {
        return productoList;
    }

    public void setProductoList(List<Long> productoList) {
        this.productoList = productoList;
    }

    @Override
    public String toString() {
        return productoList.stream()
                .map((id) -> String.format("{id=%s}", id))
                .collect(Collectors.joining());
    }
}
