package com.ingenieria.gateway.repository.rowmapper;

import com.ingenieria.gateway.domain.DetalleFactura;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link DetalleFactura}, with proper type conversions.
 */
@Service
public class DetalleFacturaRowMapper implements BiFunction<Row, String, DetalleFactura> {

    private final ColumnConverter converter;

    public DetalleFacturaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DetalleFactura} stored in the database.
     */
    @Override
    public DetalleFactura apply(Row row, String prefix) {
        DetalleFactura entity = new DetalleFactura();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCantidad(converter.fromRow(row, prefix + "_cantidad", Float.class));
        entity.setIdProducto(converter.fromRow(row, prefix + "_id_producto", Long.class));
        entity.setFacturaId(converter.fromRow(row, prefix + "_factura_id", Long.class));
        return entity;
    }
}
