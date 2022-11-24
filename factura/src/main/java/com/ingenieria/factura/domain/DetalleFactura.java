package com.ingenieria.factura.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DetalleFactura.
 */
@Table("detalle_factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DetalleFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("cantidad")
    private Float cantidad;

    @Transient
    @JsonIgnoreProperties(value = { "detalleFacturas" }, allowSetters = true)
    private Producto producto;

    @Transient
    @JsonIgnoreProperties(value = { "detalleFacturas" }, allowSetters = true)
    private Factura factura;

    @Column("producto_id")
    private Long productoId;

    @Column("factura_id")
    private Long facturaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DetalleFactura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getCantidad() {
        return this.cantidad;
    }

    public DetalleFactura cantidad(Float cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        this.productoId = producto != null ? producto.getId() : null;
    }

    public DetalleFactura producto(Producto producto) {
        this.setProducto(producto);
        return this;
    }

    public Factura getFactura() {
        return this.factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
        this.facturaId = factura != null ? factura.getId() : null;
    }

    public DetalleFactura factura(Factura factura) {
        this.setFactura(factura);
        return this;
    }

    public Long getProductoId() {
        return this.productoId;
    }

    public void setProductoId(Long producto) {
        this.productoId = producto;
    }

    public Long getFacturaId() {
        return this.facturaId;
    }

    public void setFacturaId(Long factura) {
        this.facturaId = factura;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetalleFactura)) {
            return false;
        }
        return id != null && id.equals(((DetalleFactura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DetalleFactura{" +
            "id=" + getId() +
            ", cantidad=" + getCantidad() +
            "}";
    }
}
