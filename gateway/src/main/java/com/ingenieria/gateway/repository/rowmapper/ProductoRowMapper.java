package com.ingenieria.gateway.repository.rowmapper;

import com.ingenieria.gateway.domain.Producto;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Producto}, with proper type conversions.
 */
@Service
public class ProductoRowMapper implements BiFunction<Row, String, Producto> {

    private final ColumnConverter converter;

    public ProductoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Producto} stored in the database.
     */
    @Override
    public Producto apply(Row row, String prefix) {
        Producto entity = new Producto();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMarca(converter.fromRow(row, prefix + "_marca", String.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setDescripcion(converter.fromRow(row, prefix + "_descripcion", String.class));
        entity.setPrecio(converter.fromRow(row, prefix + "_precio", Float.class));
        entity.setStock(converter.fromRow(row, prefix + "_stock", Integer.class));
        return entity;
    }
}
