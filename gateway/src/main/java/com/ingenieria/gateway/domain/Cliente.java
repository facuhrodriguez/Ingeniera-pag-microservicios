package com.ingenieria.gateway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cliente.
 */
@Table("cliente")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre")
    private String nombre;

    @Column("apellido")
    private String apellido;

    @Column("direccion")
    private String direccion;

    @Column("activo")
    private Integer activo;

    @Transient
    private Telefono telefono;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cliente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Cliente nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public Cliente apellido(String apellido) {
        this.setApellido(apellido);
        return this;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Cliente direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getActivo() {
        return this.activo;
    }

    public Cliente activo(Integer activo) {
        this.setActivo(activo);
        return this;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public Telefono getTelefono() {
        return this.telefono;
    }

    public void setTelefono(Telefono telefono) {
        if (this.telefono != null) {
            this.telefono.setCliente(null);
        }
        if (telefono != null) {
            telefono.setCliente(this);
        }
        this.telefono = telefono;
    }

    public Cliente telefono(Telefono telefono) {
        this.setTelefono(telefono);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        return id != null && id.equals(((Cliente) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", activo=" + getActivo() +
            "}";
    }
}
