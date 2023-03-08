package com.ingenieria.cliente.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "gasto_total_iva")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GastoTotal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("gasto_total_iva")
    private Double gasto_total_iva;

    @Field("cant_facturas")
    private Long cant_facturas;

    @Field("cliente")
    private Cliente cliente;

    public String getId() {
        return this.id;
    }

    public GastoTotal id(String id) {
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

    public GastoTotal cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    public Double getGastoTotalIva() {
        return this.gasto_total_iva;
    }

    public GastoTotal gastoTotalIva(Double gastoTotalIva) {
        this.setGastoTotalIva(gastoTotalIva);
        return this;
    }

    public void setGastoTotalIva(Double gastoTotalIva) {
        this.gasto_total_iva = gastoTotalIva;
    }

    public Long getCantFacturas() {
        return this.cant_facturas;
    }

    public GastoTotal cantFacturas(Long cantFacturas) {
        this.setCantFacturas(cantFacturas);
        return this;
    }

    public void setCantFacturas(Long cantFacturas) {
        this.cant_facturas = cantFacturas;
    }
}
