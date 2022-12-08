package com.ingenieria.producto.service.errors;

import com.ingenieria.producto.web.rest.errors.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class StockInsuficienteException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public StockInsuficienteException(Long id) {
        super(ErrorConstants.DEFAULT_TYPE, String.format("El stock del producto (%s), no es suficiente.", id), Status.BAD_REQUEST);
    }
}
