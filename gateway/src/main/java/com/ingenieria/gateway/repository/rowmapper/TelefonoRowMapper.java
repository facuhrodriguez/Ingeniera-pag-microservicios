package com.ingenieria.gateway.repository.rowmapper;

import com.ingenieria.gateway.domain.Telefono;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Telefono}, with proper type conversions.
 */
@Service
public class TelefonoRowMapper implements BiFunction<Row, String, Telefono> {

    private final ColumnConverter converter;

    public TelefonoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Telefono} stored in the database.
     */
    @Override
    public Telefono apply(Row row, String prefix) {
        Telefono entity = new Telefono();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCodigoArea(converter.fromRow(row, prefix + "_codigo_area", Integer.class));
        entity.setNroTelefono(converter.fromRow(row, prefix + "_nro_telefono", Integer.class));
        entity.setTipo(converter.fromRow(row, prefix + "_tipo", String.class));
        entity.setClienteId(converter.fromRow(row, prefix + "_cliente_id", Long.class));
        return entity;
    }
}
