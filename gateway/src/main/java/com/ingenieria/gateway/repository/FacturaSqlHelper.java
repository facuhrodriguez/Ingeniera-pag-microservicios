package com.ingenieria.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FacturaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("fecha", table, columnPrefix + "_fecha"));
        columns.add(Column.aliased("total_sin_iva", table, columnPrefix + "_total_sin_iva"));
        columns.add(Column.aliased("iva", table, columnPrefix + "_iva"));
        columns.add(Column.aliased("total_con_iva", table, columnPrefix + "_total_con_iva"));
        columns.add(Column.aliased("id_cliente", table, columnPrefix + "_id_cliente"));

        return columns;
    }
}
