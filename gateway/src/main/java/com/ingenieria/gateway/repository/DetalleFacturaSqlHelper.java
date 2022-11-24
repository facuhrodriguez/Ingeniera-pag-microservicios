package com.ingenieria.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DetalleFacturaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cantidad", table, columnPrefix + "_cantidad"));
        columns.add(Column.aliased("id_producto", table, columnPrefix + "_id_producto"));

        columns.add(Column.aliased("factura_id", table, columnPrefix + "_factura_id"));
        return columns;
    }
}
