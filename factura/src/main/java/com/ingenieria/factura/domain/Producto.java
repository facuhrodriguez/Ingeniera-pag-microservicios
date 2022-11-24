package com.ingenieria.factura.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Producto.
 */
@Table("producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("marca")
    private String marca;

    @Column("nombre")
    private String nombre;

    @Column("descripcion")
    private String descripcion;

    @Column("precio")
    private Float precio;

    @Column("stock")
    private Integer stock;

    @Transient
    @JsonIgnoreProperties(value = { "producto", "factura" }, allowSetters = true)
    private Set<DetalleFactura> detalleFacturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Producto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return this.marca;
    }

    public Producto marca(String marca) {
        this.setMarca(marca);
        return this;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Producto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Producto descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return this.precio;
    }

    public Producto precio(Float precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return this.stock;
    }

    public Producto stock(Integer stock) {
        this.setStock(stock);
        return this;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Set<DetalleFactura> getDetalleFacturas() {
        return this.detalleFacturas;
    }

    public void setDetalleFacturas(Set<DetalleFactura> detalleFacturas) {
        if (this.detalleFacturas != null) {
            this.detalleFacturas.forEach(i -> i.setProducto(null));
        }
        if (detalleFacturas != null) {
            detalleFacturas.forEach(i -> i.setProducto(this));
        }
        this.detalleFacturas = detalleFacturas;
    }

    public Producto detalleFacturas(Set<DetalleFactura> detalleFacturas) {
        this.setDetalleFacturas(detalleFacturas);
        return this;
    }

    public Producto addDetalleFactura(DetalleFactura detalleFactura) {
        this.detalleFacturas.add(detalleFactura);
        detalleFactura.setProducto(this);
        return this;
    }

    public Producto removeDetalleFactura(DetalleFactura detalleFactura) {
        this.detalleFacturas.remove(detalleFactura);
        detalleFactura.setProducto(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return id != null && id.equals(((Producto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Producto{" +
            "id=" + getId() +
            ", marca='" + getMarca() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", precio=" + getPrecio() +
            ", stock=" + getStock() +
            "}";
    }
}
