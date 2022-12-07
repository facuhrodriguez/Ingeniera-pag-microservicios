package com.ingenieria.factura.service.dto.ordencompra;

import org.json.JSONObject;

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
        return toJSON().toString();
    }

    public JSONObject toJSON() {

        var lis = productoCantidadList
                .stream()
                .map(ProductoCantidadDTO::toJSON)
                .collect(Collectors.toList());

        return new JSONObject(lis);
    }

    public List<Long> getListProductsOnly() {
        return productoCantidadList.stream()
                .map(ProductoCantidadDTO::getId)
                .collect(Collectors.toList());
    }
}
