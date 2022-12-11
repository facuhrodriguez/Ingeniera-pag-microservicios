package com.ingenieria.factura.service.dto.ordencompra;

import java.io.Serializable;

public class IdSolicitudDTO implements Serializable {
    private Long IdSolicitud;

    public Long getIdSolicitud() {
        return IdSolicitud;
    }

    public void setIdSolicitud(Long idSolicitud) {
        IdSolicitud = idSolicitud;
    }
}
