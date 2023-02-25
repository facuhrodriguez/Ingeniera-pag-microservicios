package com.ingenieria.factura.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Factura.
 */
@Table("factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha")
    private LocalDate fecha;

    @Column("total_sin_iva")
    private Double totalSinIva;

    @Column("iva")
    private Double iva;

    @Column("total_con_iva")
    private Double totalConIva;

    @Column("id_cliente")
    private String idCliente;

    @Transient
    @JsonIgnoreProperties(value = { "factura" }, allowSetters = true)
    private Set<DetalleFactura> detalleFacturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Factura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public Factura fecha(LocalDate fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getTotalSinIva() {
        return this.totalSinIva;
    }

    public Factura totalSinIva(Double totalSinIva) {
        this.setTotalSinIva(totalSinIva);
        return this;
    }

    public void setTotalSinIva(Double totalSinIva) {
        this.totalSinIva = totalSinIva;
    }

    public Double getIva() {
        return this.iva;
    }

    public Factura iva(Double iva) {
        this.setIva(iva);
        return this;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public Double getTotalConIva() {
        return this.totalConIva;
    }

    public Factura totalConIva(Double totalConIva) {
        this.setTotalConIva(totalConIva);
        return this;
    }

    public void setTotalConIva(Double totalConIva) {
        this.totalConIva = totalConIva;
    }

    public String getIdCliente() {
        return this.idCliente;
    }

    public Factura idCliente(String idCliente) {
        this.setIdCliente(idCliente);
        return this;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Set<DetalleFactura> getDetalleFacturas() {
        return this.detalleFacturas;
    }

    public void setDetalleFacturas(Set<DetalleFactura> detalleFacturas) {
        if (this.detalleFacturas != null) {
            this.detalleFacturas.forEach(i -> i.setFactura(null));
        }
        if (detalleFacturas != null) {
            detalleFacturas.forEach(i -> i.setFactura(this));
        }
        this.detalleFacturas = detalleFacturas;
    }

    public Factura detalleFacturas(Set<DetalleFactura> detalleFacturas) {
        this.setDetalleFacturas(detalleFacturas);
        return this;
    }

    public Factura addDetalleFactura(DetalleFactura detalleFactura) {
        this.detalleFacturas.add(detalleFactura);
        detalleFactura.setFactura(this);
        return this;
    }

    public Factura removeDetalleFactura(DetalleFactura detalleFactura) {
        this.detalleFacturas.remove(detalleFactura);
        detalleFactura.setFactura(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return id != null && id.equals(((Factura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
            ", fecha='" + getFecha() + "'" +
            ", totalSinIva=" + getTotalSinIva() +
            ", iva=" + getIva() +
            ", totalConIva=" + getTotalConIva() +
            ", idCliente=" + getIdCliente() +
            "}";
    }
}
