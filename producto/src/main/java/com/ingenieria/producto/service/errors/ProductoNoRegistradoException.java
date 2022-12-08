package com.ingenieria.producto.service.errors;

import com.ingenieria.producto.web.rest.errors.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ProductoNoRegistradoException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public ProductoNoRegistradoException(Long id) {
        super(ErrorConstants.DEFAULT_TYPE, String.format("Producto (%s) no encontrado.", id), Status.BAD_REQUEST);
    }
}
