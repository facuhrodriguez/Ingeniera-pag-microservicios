package com.ingenieria.cliente.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "gasto_total_iva")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GastoTotalIva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("gasto_total_iva")
    private Double gasto_total_iva;

    @Field("cliente")
    private Cliente cliente;

    public String getId() {
        return this.id;
    }

    public GastoTotalIva id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public GastoTotalIva cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    public Double getGastoTotalIva() {
        return this.gasto_total_iva;
    }

    public GastoTotalIva gastoTotalIva(Double gastoTotalIva) {
        this.setGastoTotalIva(gastoTotalIva);
        return this;
    }

    public void setGastoTotalIva(Double gastoTotalIva) {
        this.gasto_total_iva = gastoTotalIva;
    }
}
