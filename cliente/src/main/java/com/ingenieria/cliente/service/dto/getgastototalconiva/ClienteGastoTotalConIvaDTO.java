package com.ingenieria.cliente.service.dto.getgastototalconiva;

import java.io.Serializable;

public class ClienteGastoTotalConIvaDTO implements Serializable {

    private String nombre;
    private String apellido;
    private Double gastoTotal;

    public ClienteGastoTotalConIvaDTO(String nombre, String apellido, Double gastoTotal) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.gastoTotal = gastoTotal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Double getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }
}
