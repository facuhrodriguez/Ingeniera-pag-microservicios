package com.ingenieria.producto.service.dto.ordencompra;

import java.io.Serializable;

public class IdSolicitudDTO implements Serializable {

    private long idSolicitud;

    public IdSolicitudDTO(long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
}
