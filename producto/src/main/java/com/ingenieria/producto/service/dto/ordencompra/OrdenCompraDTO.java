package com.ingenieria.producto.service.dto.ordencompra;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenCompraDTO implements Serializable {
    private List<ProductoCantidadDTO> productoCantidadList;

    public List<ProductoCantidadDTO> getProductoCantidadList() {
        return productoCantidadList;
    }

    public void setProductoCantidadList(List<ProductoCantidadDTO> productoCantidadList) {
        this.productoCantidadList = productoCantidadList;
    }

    @Override
    public String toString() {
        return productoCantidadList.toString();
    }

    public List<Long> getListProductsOnly() {
        return productoCantidadList.stream()
                .map(ProductoCantidadDTO::getId)
                .collect(Collectors.toList());
    }
}
