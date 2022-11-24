package com.ingenieria.factura.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("marca", table, columnPrefix + "_marca"));
        columns.add(Column.aliased("nombre", table, columnPrefix + "_nombre"));
        columns.add(Column.aliased("descripcion", table, columnPrefix + "_descripcion"));
        columns.add(Column.aliased("precio", table, columnPrefix + "_precio"));
        columns.add(Column.aliased("stock", table, columnPrefix + "_stock"));

        return columns;
    }
}
