package com.ingenieria.gateway.repository.rowmapper;

import com.ingenieria.gateway.domain.Factura;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Factura}, with proper type conversions.
 */
@Service
public class FacturaRowMapper implements BiFunction<Row, String, Factura> {

    private final ColumnConverter converter;

    public FacturaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Factura} stored in the database.
     */
    @Override
    public Factura apply(Row row, String prefix) {
        Factura entity = new Factura();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFecha(converter.fromRow(row, prefix + "_fecha", LocalDate.class));
        entity.setTotalSinIva(converter.fromRow(row, prefix + "_total_sin_iva", Double.class));
        entity.setIva(converter.fromRow(row, prefix + "_iva", Double.class));
        entity.setTotalConIva(converter.fromRow(row, prefix + "_total_con_iva", Double.class));
        return entity;
    }
}
