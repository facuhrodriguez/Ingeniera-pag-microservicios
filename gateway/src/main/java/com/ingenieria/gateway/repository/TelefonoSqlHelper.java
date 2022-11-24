package com.ingenieria.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class TelefonoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("codigo_area", table, columnPrefix + "_codigo_area"));
        columns.add(Column.aliased("nro_telefono", table, columnPrefix + "_nro_telefono"));
        columns.add(Column.aliased("tipo", table, columnPrefix + "_tipo"));

        columns.add(Column.aliased("cliente_id", table, columnPrefix + "_cliente_id"));
        return columns;
    }
}
