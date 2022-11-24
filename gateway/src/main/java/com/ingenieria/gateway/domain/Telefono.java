package com.ingenieria.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Telefono.
 */
@Table("telefono")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Telefono implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("codigo_area")
    private Integer codigoArea;

    @Column("nro_telefono")
    private Integer nroTelefono;

    @Column("tipo")
    private String tipo;

    @Transient
    private Cliente cliente;

    @Column("cliente_id")
    private Long clienteId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Telefono id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigoArea() {
        return this.codigoArea;
    }

    public Telefono codigoArea(Integer codigoArea) {
        this.setCodigoArea(codigoArea);
        return this;
    }

    public void setCodigoArea(Integer codigoArea) {
        this.codigoArea = codigoArea;
    }

    public Integer getNroTelefono() {
        return this.nroTelefono;
    }

    public Telefono nroTelefono(Integer nroTelefono) {
        this.setNroTelefono(nroTelefono);
        return this;
    }

    public void setNroTelefono(Integer nroTelefono) {
        this.nroTelefono = nroTelefono;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Telefono tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.clienteId = cliente != null ? cliente.getId() : null;
    }

    public Telefono cliente(Cliente cliente) {
        this.setCliente(cliente);
        return this;
    }

    public Long getClienteId() {
        return this.clienteId;
    }

    public void setClienteId(Long cliente) {
        this.clienteId = cliente;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Telefono)) {
            return false;
        }
        return id != null && id.equals(((Telefono) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Telefono{" +
            "id=" + getId() +
            ", codigoArea=" + getCodigoArea() +
            ", nroTelefono=" + getNroTelefono() +
            ", tipo='" + getTipo() + "'" +
            "}";
    }
}
